# loop-msh-u27 — Powerful Broker (targeted proliferate)

Base branch: **`loop-msh-u25`** (not `main`). Stacked. Since u25 sits on u32 → u23, which was itself
rebased onto `origin/main`, `main` *is* now an ancestor, but the u23, u32 and u25 commits below this
branch are not upstream yet — this still waits for those to land before it can be opened on its own.

- **Primitive:** `ProliferateEffect` gains `target: EffectTarget? = null`
  (`mtg-sdk/.../scripting/effects/CounterEffects.kt`). `null` keeps CR 701.34 proliferate
  (resolution-time choose-any-number, no targeting); non-null is the targeted single-object form.
- **Primitive:** new `TargetPermanentOrPlayer(permanentFilter: TargetFilter = Permanent)`
  (`mtg-sdk/.../scripting/targets/TargetRequirement.kt`), surfaced as `Targets.PermanentOrPlayer`.
  Wired in `TargetFinder`, `TargetValidator`, `TargetEnumerationUtils`, `ChangeTargetExecutor`,
  `ReselectTargetRandomlyExecutor`.
- **Shared placement:** `ProliferateExecutor.addOneOfEachKind(state, recipients, controllerId)` is
  now the single implementation; `MiscContinuationResumer.resumeProliferate` calls it instead of
  its own copy. The moved code is line-for-line the old resumer body, so the untargeted path's
  runtime behavior is meant to be identical — I verified this by reading, not by a diff tool.
- **Card:** `mtg-sets/.../msh/cards/PowerfulBroker.kt` — `{T}`, sorcery-speed, one
  `Targets.PermanentOrPlayer` target, `Effects.Proliferate(recipient)`.
- **Tests:** `TargetedProliferateTest` (primitive: no counters, several kinds, noncreature
  permanent, player target, fizzle on an illegal target, counter-history recording, untargeted
  proliferate still pauses for its decision) and `PowerfulBrokerScenarioTest` (card: creature /
  player / land targets, sorcery-speed gate, summoning sickness, Kid Loki hexproof as the
  behavioral proof that the placement is attributed to the activating player).
- **Playtest scenario:** `manual-scenarios/sets/msh/loop-msh-u27-powerful-broker.json`.
- **Docs:** `docs/card-sdk-language-reference.md` — `Proliferate(target?)` entry rewritten (and its
  wrong CR 701.27 corrected to 701.34, verified against `MagicCompRules_20260619.txt:3592`) plus a
  `Targets.PermanentOrPlayer` entry. Retired the now-closed gap note in
  `backlog/sets/marvel-super-heroes/mechanics.md`.
- **Gate:** `just test` passed (11239 tests) on the re-run after `just rebless-cards`. The first run
  failed only on the MSH snapshot (expected for a new card) and `ConniveTargetingTest`, which timed
  out rather than asserting — the known flake — and passes standalone.
- **Snapshot:** only `MSH.json` moved, by exactly the Powerful Broker block. That is the evidence
  that `data object` → `data class` on `ProliferateEffect` is encoding-identical.

## Things I'm unsure about / deliberately did not do

- I did **not** merge `TargetCreatureOrPlayer` into the new filter-parameterized type. It would be
  strictly more general (`permanentFilter = Creature`), but it is the serialized `@SerialName` of
  the two cards using it, so folding it in churns their snapshots for no behavior change. Flagging
  it as a judgment call, not an oversight.
- ~~Neither proliferate path consults `projectedState.canReceiveCounters(...)`.~~ **Fixed in
  review corrections:** the guard now lives in `addOneOfEachKind`, so both the targeted and the
  untargeted form skip a recipient that can't have counters put on it (Blossombind). This is a
  behavior change to untargeted proliferate — it was a pre-existing bug, not a semantic worth
  preserving.
- No mtgish emitter/bridge entry: the tooling has no proliferate mapping at all today
  (`grep -i proliferate mtgish-tooling/src` is empty), so there was no capability to extend.
- No manual playthrough in the web client, no UX pass, no e2e. The targeted form adds no new
  decision type — it reuses ordinary target announcement — so no client work was expected, but
  that is reasoning, not an observation of the running app.
