#[unsafe(no_mangle)]
pub extern "C" fn gridelyx_abi_version() -> u32 {
    1
}

#[unsafe(no_mangle)]
pub extern "C" fn gridelyx_protocol_version() -> u32 {
    1
}

#[unsafe(no_mangle)]
pub extern "C" fn gridelyx_add(left: f64, right: f64) -> f64 {
    left + right
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn abi_is_stable() {
        assert_eq!(gridelyx_abi_version(), 1);
        assert_eq!(gridelyx_protocol_version(), 1);
        assert_eq!(gridelyx_add(20.0, 22.0), 42.0);
    }
}
