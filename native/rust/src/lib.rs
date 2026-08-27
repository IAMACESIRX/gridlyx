#[unsafe(no_mangle)]
pub extern "C" fn madk_abi_version() -> u32 {
    1
}

#[unsafe(no_mangle)]
pub extern "C" fn madk_add(left: f64, right: f64) -> f64 {
    left + right
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn abi_is_stable() {
        assert_eq!(madk_abi_version(), 1);
        assert_eq!(madk_add(20.0, 22.0), 42.0);
    }
}
