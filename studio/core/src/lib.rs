//! Gridelyx Studio launcher/instance-management core.
//!
//! This crate deliberately has no GUI or network implementation. It defines the stable
//! product model, provider contracts, provenance rules and deterministic dependency solver
//! that desktop, CLI and AI control planes can share.

pub mod model;
pub mod provider;
pub mod provenance;
pub mod solver;

pub use model::*;
pub use provider::*;
pub use provenance::*;
pub use solver::*;
