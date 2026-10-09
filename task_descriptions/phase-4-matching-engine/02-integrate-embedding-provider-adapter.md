# Integrate embedding provider adapter

**Task group:** Phase 4: Matching engine

## Description

Define a provider-neutral server-side adapter for generating embeddings and similarity inputs, keeping provider credentials and vendor-specific behavior out of controllers and browser code.

## Completion outcome

The matching service can use the adapter through a stable interface and can substitute a deterministic local implementation for tests or demos.

