---
name: validate-rules
description: Classify a suspected Magic rules defect or validate a card/engine interaction against Oracle text, the official Comprehensive Rules, and a reproducible stack trace before changing production code.
argument-hint: <interaction or suspected defect>
---

# Validate Magic rules correctly

Use this skill before claiming a card, simulation, scenario, or engine change is
rules-correct or rules-broken. It is mandatory for a suspected rule defect and
for reviewing a cross-cutting rules change.

Read [`docs/rules-validation-protocol.md`](../../../docs/rules-validation-protocol.md)
in full first. It is the canonical contract; this skill is the routing and
execution checklist.

## Required output

Before editing production logic, create a concise validation record containing:

```text
Claim:
Authority: Oracle <source>; CR <verified rule / named rule>
Setup and submitted actions:
Interaction trace: action → events → replacements → triggers/order → stack/priority → resolution → SBA
Expected versus actual:
Classification: RULES_DEFECT | TEST_OR_HARNESS_DEFECT | IMPLEMENTATION_GAP | UNRESOLVED
Reproduction: test class or seed + policy seed + transcript
Independent review required: yes/no; reason
```

`UNRESOLVED` means do not modify the engine. Request a separate rule-oracle
review with the authority and trace, not a request to merely rerun the test.

## Source and trace discipline

- Get current card wording from Scryfall or Gatherer, then verify the actual
  rules path in the current official Comprehensive Rules.
- A failure before an `OrderTriggeredAbilitiesDecision`, a response on the
  stack, priority returning to the active player, or an SBA checkpoint is often
  expected behaviour. Drive that step explicitly before classifying the result.
- A simulation/invariant finding is a lead, not authority. Preserve its seed,
  policy seed, deck config, actions/responses, and state/event digests.
- Do not remove a failing assertion simply to make a test green. If it is a
  harness defect, replace it with an assertion of the correct authoritative
  sequence.

## Independent review

Require a separate reviewer for replacements, Commander/multiplayer ownership,
continuations, trigger ordering, stack/priority, layers, LKI, costs, copies, or
SBAs. The reviewer independently checks the authority and trace, then confirms
the regression fails without the proposed production change.

## Completion

Leave a focused `rules-engine` regression that pins the full claimed outcome
and the edge branch that motivated it. Run the appropriate `just` gate from the
`verify` skill. State any still-unimplemented route explicitly rather than
calling the general rule verified.
