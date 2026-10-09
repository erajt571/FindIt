# Add deduplication logic for repeated job runs

**Task group:** Phase 4: Matching engine

## Description

Make match generation idempotent so retries or repeated candidate-processing jobs do not create duplicate records or duplicate user notifications.

## Completion outcome

Running the same matching job multiple times preserves one canonical pair and stable associated state.

