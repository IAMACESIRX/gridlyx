using System;
using System.Buffers.Binary;
using System.IO;
using System.Text;

namespace Madk.Bridge;

public sealed record BridgeFrame(int Version, long RequestId, string Operation, byte[] Payload)
{
    public byte[] Encode()
    {
        byte[] operation = Encoding.UTF8.GetBytes(Operation);
        if (operation.Length > 256 || Payload.Length > 16 * 1024 * 1024)
        {
            throw new InvalidDataException("Bridge frame exceeds protocol limits");
        }

        byte[] frame = new byte[20 + operation.Length + Payload.Length];
        BinaryPrimitives.WriteInt32BigEndian(frame.AsSpan(0, 4), Version);
        BinaryPrimitives.WriteInt64BigEndian(frame.AsSpan(4, 8), RequestId);
        BinaryPrimitives.WriteInt32BigEndian(frame.AsSpan(12, 4), operation.Length);
        operation.CopyTo(frame.AsSpan(16, operation.Length));
        int payloadLengthOffset = 16 + operation.Length;
        BinaryPrimitives.WriteInt32BigEndian(frame.AsSpan(payloadLengthOffset, 4), Payload.Length);
        Payload.CopyTo(frame.AsSpan(payloadLengthOffset + 4, Payload.Length));
        return frame;
    }
}
