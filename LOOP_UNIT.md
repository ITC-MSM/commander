# loop-msh-u24 — Loki, God of Mischief

- **Primitive (1/2):** `StackResolver.emitBecomesTarget` now emits a `BecomesTargetEvent` for
  `ChosenTarget.Player` too, stamped `targetIsPlayer = true` (new field on the engine event).
  `rules-engine/.../mechanics/stack/StackResolver.kt`, `.../core/GameEvent.kt`.
- **Primitive (2/2):** `EventPattern.BecomesTargetEvent` gains `includePlayerTargets` (widens *what
  got targeted*) and `abilitiesOnly` (narrows *what did the targeting*, mirror of `spellsOnly`).
  `mtg-sdk/.../scripting/EventPattern.kt`; matched in `TriggerMatcher.matchesBecomesTargetTrigger`.
- **DSL:** `Triggers.BecomesTargetOfAbility(filter, byYou, includePlayers)` in
  `mtg-sdk/.../dsl/Triggers.kt`, the mirror of the existing `BecomesTargetOfSpell`.
- **Card:** `mtg-sets/.../definitions/msh/cards/LokiGodOfMischief.kt` — {1}{U} 2/1 legend; the new
  trigger plus `oncePerTurn = true` and `Effects.DrawCards(1)`. No new effect type.
- **Target-emission sites audited:** all 7 places a stack object gets a `TargetsComponent` (found by
  `git grep "TargetsComponent.capture"`, which is the only construction path). The 4 declaration
  sites in `StackResolver` (cast, triggered, activated, spell copy) all funnel through
  `emitBecomesTarget` and now cover players. The 3 retarget/reselect sites
  (`ManaPaymentContinuationResumer.resumeChangeSpellTarget`, `ContestedRetargetLogic.advance`,
  `ReselectTargetRandomlyExecutor`) emit **nothing today for any target kind** — pre-existing, left
  unchanged, pinned by a characterization test.
- **Blast radius:** `EventPattern.BecomesTargetEvent` has exactly one matcher
  (`TriggerMatcher.matchesBecomesTargetTrigger`, verified by grep across every module's `src/main`),
  and the player-target guard sits ahead of the filter check, so no existing card can see a player
  target. Ward is `TriggerBinding.SELF` on a permanent, so it can't match a player id either.
- **Tests:** `rules-engine/.../triggers/BecomesTargetPlayerAndAbilityAxesTest.kt` (emission +
  matching axes, plus the redirect characterization) and
  `rules-engine/.../scenarios/LokiGodOfMischiefScenarioTest.kt` (the card, via Prodigal Sorcerer).
- **Playtest scenario:** `manual-scenarios/sets/msh/loop-msh-u24-loki-god-of-mischief.json`.
- **Docs:** `docs/card-sdk-language-reference.md` — `BecomesTarget` entry extended, new
  `BecomesTargetOfAbility` entry, redirect gap recorded.
- **Gate:** `just test` passed on the second run (first run failed only on the expected MSH snapshot
  rebless plus `ConniveTargetingTest`'s 120s timeout flake, which passes standalone); the second run
  genuinely executed `:rules-engine:test` and `:mtg-sets:test`. Details in
  `build/pr/loop-msh-u24-body.md`.
- **Base:** rebased onto `loop-msh-u26` (local, **not** merged upstream). u26 → u31 → u30 → u28 →
  `origin/main`, so `main` *is* an ancestor now, but none of those commits are upstream yet; this
  waits for them to land before it can be opened on its own. Reviewer: `git diff loop-msh-u26...HEAD`.
  That rebase also moved the card and its scenario test into the per-era modules `origin/main` now
  uses — `mtg-sets/2026/src/main/.../msh/cards/LokiGodOfMischief.kt` and
  `mtg-sets/2026/tests/src/test/.../LokiGodOfMischiefScenarioTest.kt`, both byte-identical to their
  pre-rebase contents. (`rules-engine`'s test source set does still depend on `:mtg-sets`, so the
  test compiled where it was; the move is for the convention and the compile-sharding rationale in
  `mtg-sets/2026/tests/build.gradle.kts`, not to fix a build error.) The mechanic-level
  `BecomesTargetPlayerAndAbilityAxesTest.kt` stays in `rules-engine/src/test/.../triggers/`.
- **Gate re-run owed:** the green below predates the rebase onto the rewritten `loop-msh-u26` (new
  base, and the card/test changed modules), so it no longer covers this tree. The diff reaches
  `mtg-sdk` (`EventPattern`, `Triggers`) and `mtgish-tooling` as well as `rules-engine` and
  `mtg-sets`, so the re-run is the **full** `test` suite — and again after the eventual rebase onto
  `origin/main`.
- **Settled (was "unsure"):** the retarget/reselect paths *should* emit — this is a **known bug**, not
  an open rules question. CR 115.9c counts the targets chosen when a spell or ability was put on the
  stack "(as modified by effects that changed those targets)", so a redirected object *is* one of its
  targets, and by CR 603.2e the "becomes a target" event happens the moment the redirect makes it
  one. Ward and every other becomes-target trigger therefore miss every redirect today, for
  permanents as much as for players. Pre-existing and orthogonal to this unit, so it stays audited
  and pinned; the characterization test now says in as many words that it locks in
  current-and-**wrong** behaviour and should be inverted, not deleted, when a follow-up unit fixes it.
- **Settled (was "unsure"):** `includePlayerTargets` with a non-`Any` `targetFilter` used to fire on
  permanents only while `description` still promised the player half. It is now a load-time
  `require` in `EventPattern.BecomesTargetEvent`'s `init` (matching the `spellsOnly`/`abilitiesOnly`
  one), so a future "a player or *creature*" wording fails loudly instead of half-working.
- **Not done:** no manual playthrough in the web client, no UX pass, no e2e. `BecomesTargetEvent`
  maps to no `ClientEvent`, and the card adds no new decision, so no client change was needed.
