# MTG rules-validation protocol

This protocol is mandatory for an agent that claims a rules defect, validates a
card interaction, or changes an engine rule. Its purpose is to separate a real
rules defect from correct priority/stack behaviour, a bad test expectation, or
an incomplete reproduction.

## Required validation record

Every delegated rules task begins with this preamble:

> Read `AGENTS.md` and this protocol in full. Role = `RULE_ORACLE`,
> `REPRODUCER`, `IMPLEMENTER`, or `ENGINE_REVIEWER`. Do not exceed that role.
> Return the complete `RULES-VALIDATION` record; do not call anything verified
> with missing fields.

The delegating agent must paste this preamble into the task itself and require a
first-line acknowledgement: `Read: AGENTS.md + rules-validation-protocol.md`.
Do not rely on repository discovery, inherited context, a hook, or a model's
default behaviour. If the acknowledgement or required record is missing, treat
the handoff as `UNRESOLVED` and do not accept its validation claim.

Use this record in the handoff. `N/A` must include its reason.

```text
RULES-VALIDATION
Claim-ID / role / repo commit:
Authority: CR effective date + clauses + SHA/retrieved; Oracle ID/text/rulings + retrieved:
Blind expected result:
Checkpoint: ALWAYS | STABLE_STATE_ONLY; why stable:
Pre-state: active player, priority, phase/step, owners/controllers:
Legal input: offered actionId or decision-shape proof:
Interaction trace: action/cost -> replacements -> events -> triggers/APNAP -> stack bottom→top -> passes -> resolution/fizzle -> SBA/trigger loops -> final priority:
Observed result:
Reproduction: deck/config, starting seat, game seed, policy seed, minimized actions/decisions, before/after fields + digests, repeated runs:
Controls: legal positive / corrupted negative:
Classification: one allowed enum; rationale:
Regression: test path/name; red-before-fix / green-after-fix:
Independent sign-offs: rules oracle / engine reviewer:
Known gaps:
```

## 1. Blind rule-oracle pass before inspecting the engine result

For every semantic claim, a rules-oracle reviewer receives only the initial
state, Oracle text, intended action/interaction, and relevant game context. It
must not receive the failing assertion, engine output, or proposed fix. The
reviewer independently retrieves the sources and records:

```text
Blind expected outcome:
Authority: official CR <rule/name, retrieval date + version/hash>; Oracle/Gatherer
<card identity/text, retrieval date>
Required interaction path:
```

Only then may the reproducer compare the expected outcome with the engine
trace. This prevents the initial failure from anchoring the rule interpretation.

## 2. State the falsifiable claim

After the blind pass, write:

> Given `<board/input>`, the independent expected outcome is `<outcome>`; the
> engine produces `<actual outcome>`.

Do not call a failed assertion a rules bug without this sentence. If the exact
rule number cannot be verified, name the rule and say that its number is
unverified; do not guess.

## 3. Establish the authority

Use authority by type: current official Comprehensive Rules for game rules;
current Oracle/Gatherer for card wording and official rulings; Scryfall only as
a current Oracle mirror. Record the CR effective date/file hash and Oracle
identity/text retrieval date. If a source cannot be verified, classification
stays `UNRESOLVED`.

Record the relevant rule/ruling in the scenario-test comment or task handoff.
Oracle text is not a substitute for the rules that govern timing, replacements,
targets, priority, state-based actions, or multiplayer ordering.

## 4. Trace the full interaction

For any nontrivial claim, capture the ordered path:

`action/cost → event(s) → replacement choices → trigger detection → APNAP and
controller-local order → stack placement → priority passes/responses →
resolution → SBAs → resulting events/state`.

Explicitly say which of these occurred. In particular, verify rather than
assume:

- a trigger-order decision before looking for the trigger on the stack;
- priority returning to the active player after resolution;
- targets being legal at declaration and rechecked on resolution;
- replacements occurring before the replaced event and not creating duplicates;
- SBAs running only at their proper checkpoint;
- all players/owners affected in Commander and multiplayer games.

The trace states phase/step, active player, priority holder, stack bottom →
top, targets/modes/cost legality, each priority pass, replacements, emitted
events, trigger capture and controller-local/APNAP placement waves,
resolution/fizzle, repeated SBA/trigger stabilization, and final priority. For
continuous effects include layer/sublayer/dependency/timestamp; for zone moves
include owner/controller and relevant LKI.

## 5. Classify before fixing

Every finding must have exactly one provisional classification:

| Classification | Evidence required | Normal outcome |
|---|---|---|
| `EXPECTED_BEHAVIOUR` | Trace agrees with the blind authority | Retain/correct the test expectation; no production fix. |
| `ENGINE_RULES_DEFECT` | Authority plus minimal engine trace contradict it | Fix production code and add a regression. |
| `ENGINE_INVARIANT_DEFECT` | A valid stable boundary violates a structural invariant | Fix engine state integrity and add controls. |
| `TEST_FIXTURE_DEFECT` | Board/action setup or assertion is not the claimed legal interaction | Correct the fixture only; do not weaken behaviour. |
| `HARNESS_OR_OBSERVER_DEFECT` | Driver/observer submits an invalid action or misreads a legal transient | Fix harness/observer only. |
| `IMPLEMENTATION_GAP` | Card/feature is absent or deliberately unsupported | Record as unimplemented; never claim it verified. |
| `UNRESOLVED` | Authority or trace is incomplete/conflicting | Stop the claim; escalate for independent review. |

No production fix proceeds until this classification is independently
confirmed. An invariant failure is structural evidence, not proof of a specific
CR rule; it becomes `ENGINE_RULES_DEFECT` only after the relevant rule path is
established.

## 6. Make it reproducible

Attach the smallest possible artefact:

- a `rules-engine` scenario/unit test for a known interaction; or
- a seed, exact decks/configuration, ordered `GameAction`/decision trace, and
  state/event digest from `SeededSimulationRunner` for exploratory failures.

For simulations preserve repository commit, CR/Oracle source metadata, exact
initial state or decks/config/starting seat, game and policy seeds, submitted
actions/responses, and relevant complete before/after fields in addition to
digests. Run the minimized transcript twice. Decision IDs are transport handles
and may be fresh per run; compare submitted responses and state/event digests,
not raw decision-id strings.

Expected behaviour must come from the blind authority, not production helpers
or snapshots being tested. Preserve red-before-fix and green-after-fix evidence
where a production change is made.

## 7. Invariant scope and controls

Every invariant declares one checkpoint scope:

- `ALWAYS` — valid after every external action boundary, including pauses.
- `STABLE_STATE_ONLY` — valid only after the action has completed its legal
  resolution/trigger/SBA boundary; it must not flag a permitted continuation or
  mid-resolution pause as corrupt.

Every new invariant has both a legal positive control (including a tricky
transient when relevant) and a deliberately corrupted negative control. A
simulation finding is ignored until its submitted action came from
`legalActions`, or its decision response satisfies `PendingDecision.shape`.

## 8. Independent review thresholds

Every semantic claim gets the blind rules-oracle pass. Ask a separate engine
reviewer, who did not author the fix, to verify the authority and trace before
a production change when any one of these is true:

- replacement effect, Commander zone choice, layers, LKI, copy effect, cost, or
  state-based action;
- more than one player can make a choice or own an affected object;
- an interaction crosses trigger ordering, priority, stack resolution, or a
  continuation;
- the proposed fix changes a shared engine primitive;
- the initial diagnosis changed classification during investigation.

For stack/priority/SBA/replacement/APNAP/Commander paths, both the rules oracle
and engine reviewer sign off. Reviewers independently fetch sources; they do
not merely rerun the same test or trust the first agent's citations. They
confirm the authority, claimed stack order, and that the regression fails
without the proposed production change.

## 9. Completion evidence

A completed rules change includes:

1. source authority and rule path;
2. a focused regression covering normal and relevant edge branch;
3. the appropriate module gate through `just`;
4. for cross-cutting mechanics, a simulation/invariant trace or replay check;
5. an explicit statement of remaining unimplemented routes, if any.

Passing tests demonstrate only the scenarios they exercise. They never turn an
unimplemented card or rule path into verified coverage.
