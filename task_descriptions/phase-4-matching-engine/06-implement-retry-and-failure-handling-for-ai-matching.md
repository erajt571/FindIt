# Implement retry and failure handling for AI matching

**Task group:** Phase 4: Matching engine

## Description

Handle provider timeouts and transient errors with bounded retry behavior and an observable terminal failure path. Keep AI-provider failures independent of report persistence.

## Completion outcome

Retry limits and outcomes are documented, failures are diagnosable, and the report remains successfully created if matching is unavailable.

