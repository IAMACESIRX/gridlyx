use crate::{ArtifactRef, SourceKind};

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ProvenanceRecord {
    pub source: SourceKind,
    pub canonical_uri: String,
    pub retrieved_at_utc: String,
    pub expected_sha256: Option<String>,
    pub observed_sha256: Option<String>,
    pub license: Option<String>,
    pub redistribution_allowed: Option<bool>,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum ProvenanceDecision {
    Allowed,
    NeedsUserApproval(String),
    Blocked(String),
}

pub fn evaluate_download(artifact: &ArtifactRef, provenance: &ProvenanceRecord) -> ProvenanceDecision {
    if artifact.source != provenance.source {
        return ProvenanceDecision::Blocked("artifact/provider provenance mismatch".into());
    }
    if provenance.redistribution_allowed == Some(false) {
        return ProvenanceDecision::Blocked("upstream provider or author disallows third-party distribution".into());
    }
    if let (Some(expected), Some(observed)) = (&provenance.expected_sha256, &provenance.observed_sha256) {
        if !expected.eq_ignore_ascii_case(observed) {
            return ProvenanceDecision::Blocked("SHA-256 verification failed".into());
        }
    }
    if provenance.observed_sha256.is_none() {
        return ProvenanceDecision::NeedsUserApproval("artifact has no locally recorded SHA-256 yet".into());
    }
    ProvenanceDecision::Allowed
}
