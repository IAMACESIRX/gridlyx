use crate::{ArtifactRef, DependencyEdge, GameVersion, SourceKind};

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum ProviderCapability {
    GameVersions,
    JavaRuntimes,
    Loaders,
    Search,
    ContentVersions,
    Dependencies,
    Downloads,
    Modpacks,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ProviderPolicy {
    pub requires_api_key: bool,
    pub redistribution_must_be_allowed: bool,
    pub cache_metadata: bool,
    pub verify_hash_when_available: bool,
}

pub trait ArtifactProvider: Send + Sync {
    fn id(&self) -> &'static str;
    fn source_kind(&self) -> SourceKind;
    fn capabilities(&self) -> &'static [ProviderCapability];
    fn policy(&self) -> ProviderPolicy;

    fn list_game_versions(&self) -> Result<Vec<GameVersion>, String> {
        Err(format!("{} does not provide game versions", self.id()))
    }

    fn resolve_artifact(&self, _id: &str, _version: Option<&str>) -> Result<ArtifactRef, String> {
        Err(format!("{} does not resolve artifacts", self.id()))
    }

    fn dependencies(&self, _artifact: &ArtifactRef) -> Result<Vec<DependencyEdge>, String> {
        Ok(Vec::new())
    }
}

#[derive(Default)]
pub struct ProviderRegistry {
    providers: Vec<Box<dyn ArtifactProvider>>,
}

impl ProviderRegistry {
    pub fn register(&mut self, provider: Box<dyn ArtifactProvider>) {
        self.providers.push(provider);
        self.providers.sort_by_key(|provider| provider.id());
    }

    pub fn provider(&self, id: &str) -> Option<&dyn ArtifactProvider> {
        self.providers.iter().find(|provider| provider.id() == id).map(|p| p.as_ref())
    }

    pub fn iter(&self) -> impl Iterator<Item = &dyn ArtifactProvider> {
        self.providers.iter().map(|provider| provider.as_ref())
    }
}
