package main

import (
    "bytes"
    "encoding/binary"
    "fmt"
)

type BridgeFrame struct {
    Version   int32
    RequestID int64
    Operation string
    Payload   []byte
}

func (frame BridgeFrame) Encode() ([]byte, error) {
    operation := []byte(frame.Operation)
    if len(operation) > 256 || len(frame.Payload) > 16*1024*1024 {
        return nil, fmt.Errorf("bridge frame exceeds protocol limits")
    }
    buffer := &bytes.Buffer{}
    _ = binary.Write(buffer, binary.BigEndian, frame.Version)
    _ = binary.Write(buffer, binary.BigEndian, frame.RequestID)
    _ = binary.Write(buffer, binary.BigEndian, int32(len(operation)))
    _, _ = buffer.Write(operation)
    _ = binary.Write(buffer, binary.BigEndian, int32(len(frame.Payload)))
    _, _ = buffer.Write(frame.Payload)
    return buffer.Bytes(), nil
}

func main() {
    encoded, err := (BridgeFrame{1, 42, "ai/infer", []byte("payload")}).Encode()
    if err != nil || len(encoded) == 0 {
        panic("bridge codec self-test failed")
    }
    fmt.Println("PASS: Go bridge codec")
}
