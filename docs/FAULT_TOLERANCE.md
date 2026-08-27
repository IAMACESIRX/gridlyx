# Gridelyx Fault-Tolerant Sandbox and Recovery Core

## Failure model

Gridelyx accepts code from development tools, AI-generated scripts and polyglot runtimes. The engine therefore
separates ordinary script failures from JVM-fatal conditions.

`FaultBoundary` catches recoverable failures around event callbacks and invokes a recovery hook. It deliberately
rethrows `VirtualMachineError` and `ThreadDeath`. Treating out-of-memory or VM corruption as an ordinary event
exception is not a reliable anti-crash strategy.

## Asynchronous script supervision

`ScriptSupervisor` runs tasks in a bounded executor and assigns a deadline. On timeout it requests interruption
with `Future.cancel(true)` and reports a timed-out result.

Java interruption is cooperative. Code that ignores interruption can keep running. Therefore thread-level
supervision is appropriate for trusted development code and scripts whose runtime honours cancellation, but it
is not a hard security boundary.

## Strong isolation

Untrusted, native-enabled or experimentally generated code should run in a separate worker process with:

- a bounded heap;
- CPU/time quota;
- no inherited filesystem/network capability by default;
- a narrow IPC protocol;
- heartbeat and kill semantics;
- process replacement after timeout or protocol violation.

A worker-process OOM can then be terminated without exhausting the Minecraft JVM.

## Transactional world editing

`PreparedWorldTransaction` contains a forward and inverse `SectionDelta` for every section. The inverse delta is
prepared from the original immutable section snapshot before live mutation.

`TransactionalWorldSandbox` performs a full revision preflight, applies the forward deltas on the authoritative
server thread, and attempts reverse-order rollback if a recoverable failure occurs. It reports
`ROLLBACK_INCOMPLETE` when concurrent state or a failing sink prevents complete restoration.

This is intentionally compatible with the existing world-edit rule:

- workers prepare state off-thread;
- no worker mutates live chunks;
- server-thread code commits and rolls back;
- lighting is reconciled after commit or rollback;
- revision conflicts fail before mutation.

## Recovery hierarchy

1. Catch ordinary callback/script exceptions at a `FaultBoundary`.
2. Cancel cooperative script execution when its deadline expires.
3. Discard or recreate the script context/classloader where possible.
4. Roll back prepared world transactions.
5. Resynchronise affected multiplayer clients.
6. Kill and replace an external script worker if it stops responding.
7. For VM-fatal failure, rely on crash-safe journals and restart recovery rather than attempting unsafe in-JVM
   continuation.
