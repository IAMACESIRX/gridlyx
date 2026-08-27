#!/usr/bin/env python3
from __future__ import annotations

import struct
from dataclasses import dataclass

CURRENT_VERSION = 1
MAX_OPERATION_BYTES = 256
MAX_PAYLOAD_BYTES = 16 * 1024 * 1024


@dataclass(frozen=True)
class BridgeFrame:
    version: int
    request_id: int
    operation: str
    payload: bytes

    def encode(self) -> bytes:
        operation = self.operation.encode("utf-8")
        if len(operation) > MAX_OPERATION_BYTES:
            raise ValueError("operation exceeds protocol limit")
        if len(self.payload) > MAX_PAYLOAD_BYTES:
            raise ValueError("payload exceeds protocol limit")
        return (
            struct.pack(">iqi", self.version, self.request_id, len(operation))
            + operation
            + struct.pack(">i", len(self.payload))
            + self.payload
        )

    @staticmethod
    def decode(data: bytes) -> "BridgeFrame":
        if len(data) < 20:
            raise ValueError("truncated bridge frame")
        version, request_id, operation_length = struct.unpack_from(">iqi", data, 0)
        if not 0 <= operation_length <= MAX_OPERATION_BYTES:
            raise ValueError("invalid operation length")
        operation_start = 16
        operation_end = operation_start + operation_length
        if operation_end + 4 > len(data):
            raise ValueError("truncated bridge operation")
        payload_length = struct.unpack_from(">i", data, operation_end)[0]
        if not 0 <= payload_length <= MAX_PAYLOAD_BYTES:
            raise ValueError("invalid payload length")
        payload_start = operation_end + 4
        payload_end = payload_start + payload_length
        if payload_end != len(data):
            raise ValueError("bridge frame length mismatch")
        return BridgeFrame(
            version,
            request_id,
            data[operation_start:operation_end].decode("utf-8"),
            data[payload_start:payload_end],
        )


def _self_test() -> None:
    original = BridgeFrame(CURRENT_VERSION, 42, "ai/infer", b"payload")
    assert BridgeFrame.decode(original.encode()) == original


if __name__ == "__main__":
    _self_test()
    print("PASS: Python bridge codec")
