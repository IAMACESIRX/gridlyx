"use strict";

// Pure deterministic example used by the GraalJS procedural-content host.
function hash3(x, y, z, seed) {
    let value = (x * 73856093) ^ (y * 19349663) ^ (z * 83492791) ^ seed;
    value ^= value >>> 13;
    value = Math.imul(value, 1274126177);
    return value ^ (value >>> 16);
}

function cell(x, y, z, seed) {
    const value = hash3(x, y, z, seed) >>> 0;
    return value % 7 === 0 ? 1 : 0;
}

if (typeof module !== "undefined") {
    module.exports = { cell };
}
