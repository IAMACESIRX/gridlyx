use std::collections::BTreeMap;

#[derive(Debug, Clone, Copy, PartialEq, Eq, PartialOrd, Ord, Hash)]
pub enum Edition {
    Java,
    Bedrock,
}

#[derive(Debug, Clone, Copy, PartialEq, Eq, PartialOrd, Ord, Hash)]
pub enum SourceKind {
    Mojang,
    Modrinth,
    CurseForge,
    FabricMeta,
    QuiltMeta,
    ForgeOfficial,
    NeoForgeMaven,
    Adoptium,
    LocalFile,
    UserProvided,
}

#[derive(Debug, Clone, Copy, PartialEq, Eq, PartialOrd, Ord, Hash)]
pub enum ContentKind {
    Mod,
    Modpack,
    ResourcePack,
    ShaderPack,
    DataPack,
    Plugin,
    World,
    ToolkitModule,
    Loader,
    Runtime,
}

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum DependencyKind {
    Required,
    Optional,
    Incompatible,
    Embedded,
}

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum ReleaseChannel {
    Release,
    Beta,
    Alpha,
    Snapshot,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct GameVersion {
    pub id: String,
    pub edition: Edition,
    pub channel: ReleaseChannel,
    pub java_major: Option<u16>,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct LoaderSpec {
    pub id: String,
    pub version: String,
    pub game_version: String,
    pub adapter: String,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct JavaRuntimeSpec {
    pub major: u16,
    pub vendor: String,
    pub managed: bool,
    pub path: Option<String>,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ArtifactRef {
    pub id: String,
    pub display_name: String,
    pub version: String,
    pub kind: ContentKind,
    pub source: SourceKind,
    pub download_uri: Option<String>,
    pub sha256: Option<String>,
    pub license: Option<String>,
    pub metadata: BTreeMap<String, String>,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct DependencyEdge {
    pub from: String,
    pub to: String,
    pub kind: DependencyKind,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct InstanceSpec {
    pub id: String,
    pub name: String,
    pub edition: Edition,
    pub game_version: String,
    pub loader: Option<LoaderSpec>,
    pub java: Option<JavaRuntimeSpec>,
    pub min_memory_mib: u32,
    pub max_memory_mib: u32,
    pub toolkit_enabled: bool,
    pub content: Vec<ArtifactRef>,
    pub jvm_args: Vec<String>,
    pub game_args: Vec<String>,
}

impl InstanceSpec {
    pub fn validate(&self) -> Result<(), String> {
        if self.id.trim().is_empty() || self.name.trim().is_empty() {
            return Err("instance id and name must not be empty".into());
        }
        if self.max_memory_mib < self.min_memory_mib {
            return Err("max memory must be >= min memory".into());
        }
        if self.edition == Edition::Java && self.java.is_none() {
            return Err("Java Edition instances require a resolved Java runtime".into());
        }
        Ok(())
    }
}
