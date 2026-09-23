# Derived continuations: coroutines, replay, or an effect interpreter

_Evaluation for [engine-sdk-architecture-review.md](../../backlog/engine-sdk-architecture-review.md) §2, fix 5.
Written 2026-09-23, after §2's other four fixes landed. Counts are a dated snapshot; re-run the grep before
you act on one._

## The problem

Every engine flow that can stop for a player's answer hand-writes "the rest of the function" as data:

- 172 `AnswerContinuation` classes (the work that consumes an answer);
- 30 `AutomaticContinuation` classes (work parked beneath a question);
- 39 resumer files in `handlers/continuations/`, about 13.4k lines.

Work after a pause survives only if someone pushed the right frame. The untap and cleanup steps are the
latest example: until §2 moved it into `AdvanceStepContinuation` and `FinishUntapStepContinuation`, both
steps lost the rest of their turn after a choice. §2 removed the other half of that tax, the trigger
bookkeeping each pause used to need, but the frames are still written by hand.

## The constraint that decides it

A paused game is **data**, and three things depend on that:

1. **Persistence and replay.** A paused game is saved as JSON and must resume after a deploy
   (`GameStateSerializer`, `LegacySuspensionMigrationTest`, `SuspensionTraceTest`).
2. **Multi-shot resumption.** The AI forks one paused state and tries several answers against it (MCTS
   rollouts, the gym's `simulate`). Undo restores an earlier paused state and answers it again. A
   continuation must be resumable any number of times.
3. **Purity.** `(GameState, GameAction) → ExecutionResult` holds only because a continuation is a
   value inside `GameState`, not a live object beside it.

Any replacement has to keep all three.

## Option A: Kotlin suspending functions

Write flows as `suspend` code (`val target = ask(ChooseTargets(...))`) and let the compiler build the
state machine.

**Rejected.** A Kotlin `Continuation` fails two of the three constraints:

- **It is one-shot.** Resuming a continuation twice throws `IllegalStateException("Already resumed")`, so
  constraint 2 is out. The AI and undo would need to deep-copy a live coroutine, which Kotlin does not
  support.
- **It is not serializable.** Its captured locals live in compiler-synthesised classes whose shape
  changes with any edit to the function. No supported serializer exists, so constraint 1 is out: every
  deploy would strand every paused game.

Workarounds such as custom continuation interceptors or bytecode-level state capture trade the
hand-written frames for a fragile dependency on compiler internals. That is worse than today.

## Option B: replay-to-resume (derived from an answer log)

Don't store the rest of the function at all. Store `(stateBeforeAction, action, answersSoFar)`. To resume,
re-run the action from its starting state and feed it the recorded answers in order. When it asks a
question it has no answer for, that is the new pause. Durable-workflow engines (Temporal, Azure Durable
Functions) use this technique.

- **Serializable and multi-shot for free.** The log is plain data: a state, an action, and a list of
  `DecisionResponse`s. Forking means copying a list and appending a different answer.
- **Nothing is hand-written.** A flow is straight-line code that calls `ask(question)`. Before the log
  is exhausted, `ask` returns the recorded answer; after, it suspends. That suspension never needs to be
  stored, so it can be a plain exception or a sealed result, not a Kotlin coroutine.
- **It needs strict determinism.** The engine is close: there is no wall clock
  (`NoWallClockInGameLogicTest`), state is immutable, and randomness goes through the state's seeded RNG.
  The known gap is unseeded shuffles in some test paths (see the scenario-test gotchas memory), which
  would have to become seeded everywhere.
- **Resumption costs a re-run.** An action with *n* questions re-runs *n* times, so it is O(n²) in the
  worst case. Real actions ask 1–3 questions. The outliers are "for each player, choose …" loops and the
  mana-payment window, which can ask per mana. For those, an intermediate checkpoint (store the state and
  log at the start of each iteration) bounds the cost.
- **Migration is per flow, and the two models coexist.** A handler moves over when its paused state
  becomes a `ReplaySuspension(start, action, answers)` frame. Everything else keeps its hand-written
  frames until its turn.

## Option C: an interpreter over the effect tree

Most answer continuations belong to effects, and effects are already data: the SDK's sealed `Effect`
tree. `EffectContinuation(remainingEffects, effectContext)` shows the model already works for sequences.
Generalise it:

- A primitive that needs an answer returns `Ask(question, bind = "name")` instead of pushing its own
  continuation class.
- The interpreter's one resume frame stores the **position in the effect tree** plus the pipeline
  environment (`PipelineState`, `EffectContext`). It writes the answer into the environment under `bind`
  and continues from the next position.
- The frame is serializable, because the tree and the environment already are, and it is multi-shot,
  because it is a value.

This removes the bespoke frames for effect-level questions, which are most of the 172. It does not reach
**action handlers** (casting, activation, the turn structure). Their pauses sit in the middle of Kotlin
code, not the effect tree, so they are Option B's territory.

## Recommendation

1. **Rule out coroutines** (Option A) permanently. The one-shot and non-serializable limits are properties
   of the language feature, not something to engineer around.
2. **Pilot Option C** on the effect side first. It is the bigger share of the frames and needs no new
   determinism guarantees. A good pilot is the "may" family (`MayTriggerContinuation`,
   `GatedActionContinuation`, the yes/no answers under `Gate.*`). They are numerous and uniform, and
   `SuspensionTraceTest`'s `nested-may` and `repeat-while` fixtures already pin their behaviour
   byte-for-byte. The pilot succeeds when those frames are deleted and the fixtures replay unchanged,
   except for the frame shape.
3. **Prototype Option B** on one action handler that pauses in several places. `CastSpellHandler`'s
   cast-time choices (modes, targets, X, additional costs) are the payoff case, and a good fit for the
   staged casting pipeline in §4. First do the determinism audit (seed every shuffle) and add a CI check
   that replays each suspension-trace fixture twice and compares the results.
4. **Keep `SuspensionTraceTest` as the contract** throughout. Now that its fixtures are encoded without
   defaults, a new field no longer breaks them; a changed *shape* still does, which is the point.

## What this does not change

The settle boundary (`Settler`), the waiting-trigger queue, and the sealed `Outcome` are independent of
how a flow's remainder is stored. Both B and C produce ordinary paused `ExecutionResult`s, which the
boundary already handles.
