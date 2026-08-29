//! Gridelyx Studio launcher/instance-management core.
//!
//! This crate deliberately has no GUI or network implementation. It defines the stable
//! product model, provider contracts, provenance rules and deterministic dependency solver
//! that desktop, CLI and AI control planes can share.

// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/studio/core/src/model.rs
pub mod model;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/studio/core/src/provider.rs
pub mod provider;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/studio/core/src/provenance.rs
pub mod provenance;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/studio/core/src/solver.rs
pub mod solver;

pub use model::*;
pub use provider::*;
pub use provenance::*;
pub use solver::*;
