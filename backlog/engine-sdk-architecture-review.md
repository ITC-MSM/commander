# Engine + SDK Architecture Review

_Review date: 2026-09-23. Scope: `rules-engine`, `mtg-sdk`, and the card corpus that uses them, read
against the goal "the most elegant, modern MTG engine that exists, and a joy to extend". This review
replaces the May–June 2026 SDK reviews (architecture review, language design, quality audit,
reusability consolidation, the June analysis and its revision, the effect-atom audit); those remain
in git history. All counts are a **dated snapshot**. Re-run the grep before you act on a number._

## Verdict

The architecture is ahead of Forge and XMage:
- a pure, immutable `(GameState, GameAction) → ExecutionResult` core with atomic rollback on error;
- cards as a typed, sealed tree that serializes to JSON;
- per-set JSON goldens that round-trip the whole corpus.

The execution is not yet elegant, for two reasons:
- The SDK grew by **enumeration** rather than composition, so most concepts have two or three
  spellings.
- The engine is **hand-wired**. A new mechanic touches 10–30 files, and several of those wiring rules
  are enforced only by agent prompts, not by the compiler or a test.

## Already world-class — keep

- **Pure core with atomic error semantics.** `ActionProcessor` returns the input state on error.
- **Serializable continuations.** A paused game survives persistence and replay.
- **Map-based effect dispatch.** `EffectExecutorRegistry` maps `KClass → executor`.
  `EffectExecutorCoverageTest` walks the sealed leaves by reflection and fails the build on any
  unregistered one. **This is the pattern to copy everywhere.**
- **Hygiene tests:**
  - `SerializationPolymorphicRegistrationTest`
  - `NoWallClockInGameLogicTest`
  - `TapEventEnforcementTest`
  - `SummoningSicknessGateEnforcementTest`
  - `FacadeBoundaryTest`
  - `CardLintTest`
- **`CardDefinitionSnapshotTest`.** Export → load → export over the whole corpus, so any lowering
  change shows up as a per-card diff.
- **Typed authoring layers that read like Oracle text:** `modal {}`, named target handles and
  `Effects.Pipeline {}` (see `CrypticCommand.kt`, `OstrichHorse.kt`). Composition-only features stay
  small; the Empower Jace commit touched 6 files.

---

## 1. ✅ Fail-closed dispatch, enforced by tests — [HIGH, cheap] — DONE

> **Done (2026-09-23).** Every dispatch site listed below is now an exhaustive `when` with no
> `else`. `ContinuationResumerCoverageTest` and `KeywordClientMirrorTest` guard the resumers and
> the client keyword mirror. `EventPattern` → `TriggerMatcher` was already exhaustive at compile
> time, so it needed no test. The work surfaced three live bugs:
> - eerie's "whenever you fully unlock a Room" never fired;
> - `readsPipeline` skipped `Divide`;
> - both amount walks skipped `GreatestAmongPlayers`.
>
> It also found 28 keywords missing from `enums.ts`. `LandTappedForMana` is still unwired on
> purpose, and that is now stated in its branch and in the docs. Out of scope: other `else`
> branches over sealed types outside these sites.

**Problem.** Effects are guarded; the other sealed hierarchies are dispatched through large `when`
blocks with catch-all `else` branches. That means a new subtype compiles and then silently does
nothing:

- **`event/TriggerIndex.kt`.** Both category maps end in `else -> emptyList()`:
  - `triggerToCategories` maps an SDK `EventPattern` to categories;
  - `engineEventCategories` maps an engine `GameEvent` to categories.

  If a new event is left out of the index, **its triggers never fire and nothing reports it**.
- **`DynamicAmountEvaluator.isDeterminable`** ends in `else -> true`.
- **`CreateDelayedTriggerExecutor.readsPipeline`** ends in `else -> false`, so an amount that reads
  the pipeline gets frozen too early.
- **`ConditionEvaluator`** (`countProgress`, and the `EffectTarget` dispatch) and
  **`PredicateEvaluator`** (`CardPredicate` / `ControllerPredicate`) end in `else -> null` or
  `else -> false`.
- **A missing continuation resumer** is only a runtime error: `ContinuationResumerRegistry` throws
  "No resumer registered". No test guards it.
- **Client mirrors have no parity test.** `Keyword` vs `web-client/src/types/enums.ts` has none;
  `CounterTypeClientMirrorTest` exists for counters only.

`TriggerMatcher` already shows the fix: it throws on a missing branch, with a comment recording that
"silent pass-through hid the IsNontoken bug for months". The pattern is known; it is just applied
unevenly.

**Fix.**
- Make every `when` over an SDK or engine sealed type exhaustive, or end it in `error(...)`.
- Extend the reflection coverage approach to:
  - `GameEvent` → `TriggerIndex` category;
  - `EventPattern` → `TriggerMatcher` branch;
  - `AnswerContinuation` / `AutomaticContinuation` → a registered resumer;
  - `Keyword` → `enums.ts`.
- Several touch points in the `add-feature` skill's cross-layer checklist ("register in
  `TriggerIndex`", "mirror in `enums.ts`") then become build failures instead of prose.

## 2. One context record, one settle boundary, a sealed result — [HIGH]

**Problem: pauses are hand-written continuation-passing.**
- There are about 155 `AnswerContinuation` classes, about 18 automatic continuations, and about 37
  resumer files (roughly 13.5k LOC in `handlers/continuations/`).
- Any caller that can pause must hand-encode "the rest of the function" as a frame. Work after the
  pause survives only if someone pushed that frame:
  - `DrawPhaseManager` returns the paused draw result and drops the step-change event and the
    priority hand-off.
  - `SubmitDecisionHandler.execute` special-cases `CLEANUP` and `UNTAP` after a resume to finish
    work the paused original flow abandoned ("if you refactor either flow, make sure both still
    trigger this advance").

**Problem: trigger context is threaded field by field.**
- Clash "if you won" was one boolean, and it touched 18 rules-engine files:
  - `TriggerContext`, `TriggerMatcher`, `TriggerProcessor`;
  - `EffectContext`, `StackComponents`;
  - `CoreContinuations` and `EffectAndTriggerContinuationResumer`;
  - `ReflexiveTriggerEffectExecutor`, `ClientStateTransformer`, and more.
- It also re-blessed 7 suspension-trace goldens, because those are encoded with
  `encodeDefaults = true` and any new field breaks them.

**Problem: trigger detection happens at many sites.**
- `.detectTriggers(` is called from 31 sites in 17 files.
- A `triggersAlreadyProcessed` flag is threaded through `ExecutionResult` and `EffectResult` to
  prevent double triggers. Its KDoc cites past double-trigger bugs.
- `SubmitDecisionHandler` and `PassPriorityHandler` each carry a "mirrors the other path" copy of the
  detect → state-based actions → stack → priority loop.

**Problem: the result type is boolean-ish.**
- `isSuccess` means `error == null && pendingDecision == null`, so a pause reads as a failure
  (`!…isSuccess` appears 65 times).
- `TurnManager` checks `if (!untapResult.isSuccess) return …` and then `if (untapResult.isPaused)`;
  the second branch is unreachable.
- Errors are untyped strings, so callers can't tell an illegal action from an engine invariant
  failure.

**Fix.**
1. Carry **one `ResolutionContext` record** through every frame and resumer. Adding a fact becomes
   one field on one record.
2. Add **one `settle(state)` step** that runs at a single engine boundary: detect triggers →
   state-based actions → put triggers on the stack → give priority. Then delete
   `triggersAlreadyProcessed` and the mirrored loops.
3. Replace `isSuccess` / `isPaused` with a **sealed `Done | Paused | Rejected(reason)`** and a typed
   rejection reason.
4. Encode the suspension-trace goldens without defaults, so an additive field doesn't break them.
5. Longer term, evaluate suspending coroutines or a small effect interpreter, so continuations are
   derived by the compiler rather than hand-written. The serializable-continuation property must
   survive; that is the design constraint, not an obstacle.

## 3. One legality kernel — [HIGH]

**Problem.** Legality is implemented three to five times:
- **`ActivationRestriction`** is switched on in four places:
  - `ActivateAbilityHandler` (16 branches);
  - `ManaSolver` (a private copy, 11);
  - `CastPermissionUtils` (11);
  - `ActivatedAbilityEnumerator` (8).
- **Cast restrictions** exist twice (`CastSpellHandler` vs `CastPermissionUtils`).
- **Enumerators never call a handler's `validate`**, so what the client is offered and what the
  server accepts are two independent implementations.
- **`ClientStateTransformer.isCastableFromLinkedExile` re-derives castability in the view layer.** It
  reads base `ControllerComponent` and printed `script.staticAbilities`, which ignores projected
  control and granted or removed abilities. That is a projected-state violation.

**Fix.** Make the handler's `validate` (or an extracted legality kernel) the single source of truth.
Enumerators generate candidates and filter them through it, and the view layer reads the result
instead of recomputing it.

## 4. Break up the god-methods — [MED–HIGH]

**Problem.** The issue is methods, not just files. Every feature passes through these, so they attract
merge conflicts and slow compiles:

| Method | Approx. lines |
|---|---|
| `CastSpellHandler.execute` | ~1,920 |
| `CastSpellHandler.validate` | ~640 |
| `ActivateAbilityHandler.executeActivation` | ~1,420 |
| `StackResolver.resolveTop` | ~1,320 |
| `ClientStateTransformer.transformCard` | ~840 |
| `ClientStateTransformer.buildCardActiveEffects` | ~690 |

- `CastSpellHandler` mixes cost totalling, validation for every alternative and additional cost,
  modal shape, pause UIs for choosing modes and targets, and construction of riders.
- A new `AdditionalCost` subtype is dispatched in 35 places in `CastSpellHandler`, 18 in
  `CastSpellEnumerator` and 15 in `CostHandler`.
- `ActivateAbilityHandler` carries its own auto-tap and a private `checkActivationRestriction`.

**Fix.**
- **Casting.** Model it as an explicit staged pipeline following the casting procedure: announce →
  modes → targets → total cost → pay → put on the stack. Each cost kind becomes a plug-in with
  `validate` / `enumerate` / `pay` / `present` (a `CostKind` registry, with a coverage test as in §1).
  That collapses the dispatch into one place per stage.
- **Activation.** Reuse the same cost plug-ins and the §3 legality kernel.
- **`ClientStateTransformer`.** Split it into per-concern projectors. It also JSON-encodes
  `CardDefinition`s to find delirium thresholds (a cache keyed by card name). Expose that as data on
  the definition instead.

## 5. One spelling per concept in the SDK — [HIGH, codemod-driven]

Sealed-hierarchy sizes: `Effect` about 344 subtypes, `StaticAbility` about 174, `Condition` about 109,
`CardPredicate` about 105, `EventPattern` about 94, `DynamicAmount` about 53. Where they overlap:

- **Four spellings of "if".** `ConditionalEffect`, `MayEffect`, `IfYouDoEffect` and
  `OptionalCostEffect` are backward-compatible functions named like constructors. All of them lower
  to `GatedEffect`, and cards use all of them plus raw `GatedEffect(Gate.…)`.
  - `ConditionalEffect.kt`'s KDoc points to a `spell { onlyIf(...) }` path that does not exist.
  - `Effects` has no `Conditional` or `May` facade.
- **Three ways to refer to an entity.** `EffectTarget`, `Player` and `EntityReference` overlap:
  `EnchantedCreature`, `LinkedExiledCard`, `TappedAsCost`, `LibraryTop` and the `Triggering…` members
  exist in more than one of them. Parker Luck refers to the same targets as both `ContextTarget(n)`
  and `ContextPlayer(n)`.
- **Filters that aren't unified.** `GameObjectFilter` is the real core.
  - `TargetFilter` and `GroupFilter` re-declare its builder methods one by one.
  - Beside them sit enums that restate it:
    - `RecipientFilter` (`CreatureYouControl`, `CreatureOpponentControls`, …);
    - `SourceFilter.HasType(type: String)`;
    - `ControllerFilter`, `CollectionFilter` and `PreventionSourceFilter`.
- **Three counter representations.** The `CounterType` enum, string constants
  (`Counters.PLUS_ONE_PLUS_ONE = "+1/+1"`, which the facades actually take), and `CounterTypeFilter`
  with `Named(name: String)`.
- **Enumeration where a parameter belongs.**
  - About 15 `Effects.Prevent*` facades (`…FromChosenArtifactSource`, `…FromChosenCreatureType`,
    `…FromChosenColoredSource`, `PreventHalf…`) should be one facade that takes a filter.
  - The `*AndChainCopy` quartet has the same shape.

**The facade boundary protects only six raw constructors.** Cards contain about 6,600 raw
`XxxEffect(` constructions against about 18,800 `Effects.` calls:
- the most common raw ones are `MoveCollectionEffect`, `GatherCardsEffect`, `ConditionalEffect`,
  `DealDamageEffect` and `SelectFromCollectionEffect`;
- raw `DynamicAmount.` beats the `DynamicAmounts.` facade by roughly 3.5×.

So "refactor underneath without touching cards" holds only for those six types.

**Type-safety holes:**
- **String data-flow keys.** `storeAs` / `from` / `storeSelected` / `collectionName` are used in
  about 581 card files. The typed `Effects.Pipeline {}` builder (about 132 files) lowers to the same
  string-keyed tree, so its safety exists only in the minority path. `CardLinter` is effectively a
  runtime type checker, built because the type system is bypassed.
- **Integer target indices.** About 900 `ContextTarget(0)`, and **136 SDK facade parameters and
  fields default to `EffectTarget.ContextTarget(0)`**. An omitted argument silently binds to the
  first target.
- **Named handles are strings too.** They are `BoundVariable("creature[0]")`, with index syntax
  inside the string.

**The pipeline can quietly replace targeting.** `JackdawSavior.kt`'s Oracle text says "return another
**target** creature card", but it is scripted as an untargeted gather → filter → select → move. Its
own KDoc calls this "a minor" divergence. It is a rules divergence: nothing is chosen when the ability
triggers, and the ability can't be countered because its target became illegal. Audit the corpus for
other "target" Oracle text scripted without a target requirement. That can become a lint rule.

**Fix, in order:**
1. **Delete the 136 `= ContextTarget(0)` defaults.** The compiler lists every call site that relied
   on them.
2. **Codemod** the string-keyed pipeline files onto `Effects.Pipeline {}`, and the `ContextTarget(n)`
   users onto named handles. Then widen `FacadeBoundaryTest` to every raw `*Effect(` constructor and
   raw `DynamicAmount.` use.
3. **Collapse the parallel types.**
   - The four "if" facades become `Effects.If` / `Effects.May`, and the old names are deleted.
   - `RecipientFilter`, `SourceFilter` and `ControllerFilter` become `GameObjectFilter`.
   - `EntityReference` merges into `EffectTarget`.
   - The counter strings and the filter enum become `CounterType`.
   - The `Prevent*FromChosenX` facades become one facade that takes a filter.
4. **Give `EffectTarget.Self` one meaning.** Today it also means "the iterated creature" inside
   `ForEachInGroup` (History of Benalia chapter III).

## 6. Generate the boilerplate — [MED]

**Problem.** There is no KSP or other code generation. These are all hand-written and hand-synced:
- about 613 polymorphic `subclass(...)` registration lines in `core/Serialization.kt`;
- the executor and resumer module lists;
- `web-client/src/types/enums.ts`;
- about 375 `applyTextReplacement` overrides, pure boilerplate for Layer 3 text-changing effects;
- about 2,297 card files that hand-copy ability `description` text, often verbatim from
  `oracleText`, which will drift;
- `docs/card-sdk-language-reference.md`, which is over 13k lines and touched by roughly one commit in
  ten. It behaves like an append-only log and is a merge-conflict hotspot on every capability PR.

**Fix.**
- **KSP or a sealed-reflection generator** for the serialization registration, the registry module
  lists and the TypeScript mirrors.
- **One generic tree rewrite** over the serializer to replace the `applyTextReplacement` overrides.
- **Ability descriptions** generated from the effect tree, overridden only where Oracle wording
  genuinely differs.
- **Split the language reference.** Generate the catalog part (every facade, its signature, its
  KDoc) from source. Keep the hand-written part for concepts and recipes.
- **Discoverability of the facades.** `Effects` has about 445 flat members, `Conditions` about 227,
  `Triggers` about 195. Organise them into nested namespaces the IDE can navigate, and pick one naming
  convention: `Triggers` mixes `val EntersBattlefield` with `fun entersBattlefield(...)`, and `Attacks`
  with `attacks(...)`.

---

## Smaller, real issues

- **Global mutable singletons.**
  - `DamageUtils.cardRegistry`, `ZoneTransitionService.cardRegistry` and
    `ZoneTransitionService.staticAbilityHandler` are `lateinit var`s, and
    `ZoneMovementUtils.tokenExecutor` is a `var`. All are set from the `EngineServices` constructor.
  - With two engines in one JVM (server plus gym, or parallel tests), the last one constructed wins.
  - Pass them through the service graph instead.
- **Object graphs rebuilt during execution.**
  - `StackResolver(cardRegistry = …)` is constructed ad hoc at 14 sites outside `EngineServices`,
    including `CounterEffectExecutor`, `StormCopyEffectExecutor`, `ExileTargetSpellExecutor` and
    `TurnManager`.
  - Each one builds a fresh `EffectHandler` with every executor module, a *different*
    `ReplacementEffectProcessor`, and no `TokenArtRegistry`. That breaks the single-instance
    invariant `EngineServices` documents.
  - Likewise `PredicateEvaluator()` and `ConditionEvaluator()` are newed up in over 100 places.
  - The late two-phase wiring (`libraryExecutors.initialize(this)`, `initializeRecursion(::recurse)`)
    is a symptom of the circular dependency between executors and the cast pipeline.
- **The CR 613.8 doc overstates the code.**
  - `docs/continuous-effect-dependency-system.md` and AGENTS.md describe dependency detection by
    trial application. `EffectSorter.dependsOn` is actually a hard-coded list of type-changing
    `Modification`s plus one `RemoveAllAbilities` → `GrantKeyword` special case.
  - Its dependency map is keyed by data-class equality, which is the "equal lord effects collide"
    hazard AGENTS.md warns about.
  - Either implement real trial application or correct the docs.
- **Throughput ceiling for gym and MCTS.**
  - `GameState` updates copy standard (non-persistent) maps: `withEntity` copies the whole entity map
    and the data class, which has about 86 fields.
  - `projectedState` is a per-instance `lazy` (synchronized by default), so any `copy()` recomputes
    the whole projection, even when only priority or the continuation stack changed.
  - The trigger index is rebuilt on every detection pass.
  - Fix: `kotlinx.collections.immutable` behind `withEntity` / `addToZone`; carry the projection
    forward when only fields that can't affect it changed; use `LazyThreadSafetyMode.NONE`.
- **The SDK isn't strictly pure.**
  - `AbilityId.generate()` is a process-global counter, which is why the snapshot test must renumber
    IDs.
  - `Effects` hardcodes a Scryfall image URL.
  - `CardLinter`, `CardValidator` and the filter query parser live in the SDK module. Move tooling
    into its own module.
- **Testing.**
  - There are two parallel harnesses in `rules-engine/src/testFixtures`: `ScenarioTestBase`
    (builder) and `GameTestDriver` (imperative). Driver-style tests hand-roll decision-answering
    loops (`answerClash()`, `chooseTrapTarget()` with iteration guards, `passWholeTurnCycle()`).
    Converge on one harness with a first-class "answer the pending decision with X" helper.
  - There are no property-based tests.
  - No CI job plays random games over the whole corpus and checks engine invariants. The gym and
    `RandomActionBenchmark` pieces already exist.
  - No conformance suite is organised by Comprehensive Rules section.
  - There are few isolated unit tests of `ConditionEvaluator`, `DynamicAmountEvaluator` or
    `TriggerMatcher`, so a regression in a shared branch surfaces as scattered card failures.
- **mtgish still costs edits.** It was touched in 3 of the 4 recent capability commits sampled,
  despite being declared non-blocking.

## Suggested order

1. **Cheap, high leverage (weeks):**
   - ~~§1 fail-closed dispatch and its coverage tests~~ (done);
   - remove the global singletons and the ad-hoc `StackResolver`s;
   - delete the `ContextTarget(0)` defaults;
   - the single `settle()` boundary from §2.
2. **Medium:**
   - `ResolutionContext` and the sealed result type (§2);
   - the legality kernel (§3);
   - the staged casting pipeline with cost plug-ins (§4).
3. **Large, codemod-driven:** SDK consolidation (§5), then generation (§6).
4. **When gym throughput matters:** persistent collections and incremental projection.
