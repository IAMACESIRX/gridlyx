use std::collections::{BTreeMap, BTreeSet, VecDeque};

use crate::{ArtifactRef, DependencyEdge, DependencyKind};

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ResolutionGraph {
    pub artifacts: BTreeMap<String, ArtifactRef>,
    pub edges: Vec<DependencyEdge>,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ResolutionPlan {
    pub install_order: Vec<String>,
    pub optional: Vec<String>,
    pub conflicts: Vec<(String, String)>,
}

pub fn resolve(graph: &ResolutionGraph) -> Result<ResolutionPlan, String> {
    let mut conflicts = Vec::new();
    let mut optional = BTreeSet::new();
    let mut indegree: BTreeMap<String, usize> = graph.artifacts.keys().cloned().map(|id| (id, 0)).collect();
    let mut adjacency: BTreeMap<String, Vec<String>> = BTreeMap::new();

    for edge in &graph.edges {
        if !graph.artifacts.contains_key(&edge.from) || !graph.artifacts.contains_key(&edge.to) {
            return Err(format!("dependency edge references unknown artifact: {} -> {}", edge.from, edge.to));
        }
        match edge.kind {
            DependencyKind::Required | DependencyKind::Embedded => {
                // Dependency must install before its dependent.
                adjacency.entry(edge.to.clone()).or_default().push(edge.from.clone());
                *indegree.get_mut(&edge.from).expect("artifact was checked") += 1;
            }
            DependencyKind::Optional => {
                optional.insert(edge.to.clone());
            }
            DependencyKind::Incompatible => {
                let pair = if edge.from <= edge.to {
                    (edge.from.clone(), edge.to.clone())
                } else {
                    (edge.to.clone(), edge.from.clone())
                };
                if !conflicts.contains(&pair) {
                    conflicts.push(pair);
                }
            }
        }
    }

    if !conflicts.is_empty() {
        return Ok(ResolutionPlan { install_order: Vec::new(), optional: optional.into_iter().collect(), conflicts });
    }

    let mut ready: VecDeque<String> = indegree
        .iter()
        .filter_map(|(id, degree)| (*degree == 0).then(|| id.clone()))
        .collect();
    let mut order = Vec::with_capacity(graph.artifacts.len());

    while let Some(id) = ready.pop_front() {
        order.push(id.clone());
        if let Some(dependents) = adjacency.get(&id) {
            for dependent in dependents {
                let degree = indegree.get_mut(dependent).expect("dependent exists");
                *degree -= 1;
                if *degree == 0 {
                    ready.push_back(dependent.clone());
                }
            }
        }
    }

    if order.len() != graph.artifacts.len() {
        return Err("required dependency cycle detected".into());
    }

    Ok(ResolutionPlan { install_order: order, optional: optional.into_iter().collect(), conflicts })
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::{ContentKind, SourceKind};

    fn artifact(id: &str) -> ArtifactRef {
        ArtifactRef {
            id: id.into(), display_name: id.into(), version: "1".into(), kind: ContentKind::Mod,
            source: SourceKind::Modrinth, download_uri: None, sha256: None, license: None,
            metadata: BTreeMap::new(),
        }
    }

    #[test]
    fn dependency_installs_before_dependent() {
        let graph = ResolutionGraph {
            artifacts: [("mod".into(), artifact("mod")), ("api".into(), artifact("api"))].into_iter().collect(),
            edges: vec![DependencyEdge { from: "mod".into(), to: "api".into(), kind: DependencyKind::Required }],
        };
        let plan = resolve(&graph).unwrap();
        assert_eq!(plan.install_order, vec!["api", "mod"]);
    }
}
