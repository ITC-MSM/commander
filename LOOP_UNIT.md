# u39 — Evil's Thrall (+ one new duration)

**Card.** Evil's Thrall [MSH 128] — {2}{R} Sorcery. "Gain control of target creature until end of
turn. If you control a Villain with greater mana value than that creature, gain control of that
creature until the end of your next turn instead. Untap that creature. It gains haste until end of
turn." A Threaten whose steal lasts a whole extra turn when your board has a bigger Villain.

**Triage (nobody triaged this card before me — I did it from Scryfall + the code).** Everything
composes from existing vocabulary *except* the duration. There is no "until the end of your next
turn" `Duration`: the existing `Duration.UntilYourNextTurn` ends at the **beginning** of your next
turn (Teferi's Protection), one whole turn short. The engine already knows the wording — it is
`MayPlayExpiry.UntilControllerStep(CLEANUP, includeCurrentTurn = false)` for may-play grants — but
that mechanism is for play permissions, not floating effects.

**Primitive — `Duration.EndOfYourNextTurn`.** A new `Duration` variant, plus
`ActiveFloatingEffect.expiresAfterTurn: Int?` (set to `turnNumber + 1` only for this duration) and
one branch in `CleanupPhaseManager.cleanupEndOfTurn`. The floor-plus-`activePlayerId == controllerId`
guard is copied deliberately from `MayPlayPermission.expiresAfterTurn`, which encodes the same
window: a floor rather than an exact turn is what survives extra turns, skipped turns and eliminated
seats. Every other duration is untouched (`expiresAfterTurn` is `null` for them), so no existing
floating effect changes behaviour. `docs/card-sdk-language-reference.md` updated in the same change,
on the `GainControlEffect` bullet.

**Composition for everything else — no new predicate, no new effect.**
- "If … instead" is one `ConditionalEffect` whose two branches are the *same* `GainControlEffect`
  differing only in `Duration` — one control change, one `ControlChangedEvent`, never a short steal
  followed by a second grab.
- "You control a Villain with greater mana value than that creature" is
  `Conditions.CompareAmounts(DynamicAmounts.battlefield(Player.You, Permanent.withSubtype(VILLAIN)).maxManaValue(), GT, DynamicAmounts.targetManaValue())`.
  `AggregateBattlefield(MAX)` returns 0 on an empty set, so controlling no Villain can never satisfy
  `> targetManaValue` — including a mana-value-0 target, since 0 is not greater than 0. The
  aggregate reads *projected* state, so a creature that only has the Villain type from a continuous
  effect counts. This is the Overload idiom (`EntityProperty(Target(0), ManaValue)` inside a
  `Compare` in a resolving spell), so nothing new was needed to read the target's mana value.
  - **I first wrote a `CardPredicate.ManaValueGreaterThanEntity` and then deleted it** when the
    aggregate composition turned out to cover the shape. The final diff has no new predicate.
- Untap + haste are the plain Act of Treason / Twisted Fealty facades, in printed order.

**Gate — read this bit carefully, the first run was incomplete.** `just test` **stopped before
`:rules-engine:test` ever ran**: `:mtg-sets:test` failed on the expected (not-yet-reblessed)
`CardDefinitionSnapshotTest` MSH golden and `:ai:test` on the documented
`AIPlayerTest > AI can evaluate board state` coroutine-timeout flake, and Gradle aborted the build
with those two. Everything else in that run was green — mtg-sdk, mtg-search, mtgish-tooling, gym,
gym-trainer, gym-server, game-server all passed, and `:rules-engine:compileTestKotlin` succeeded.
So I re-blessed and then ran the module that actually carries this change's behaviour:

- `just rebless-cards` → exit 0. Only `mtg-sets/src/test/resources/snapshots/cards/MSH.json` moved:
  **88 insertions, 0 deletions**, and the only added card `"name"` is `Evil's Thrall` (the two other
  added `name` keys are the `"target creature"` requirement inside my own card's block). No other
  set's golden moved.
- `just test-rules` → exit 0. Counted from `rules-engine/build/test-results/test/*.xml`:
  **11,187 tests, 0 failures, 0 errors, 0 skipped.** All three `EvilsThrallScenarioTest` cases passed.
- `just check-card-printing "Evil's Thrall"` → ok (MSH is the card's only printing and is the canonical).
- `just fix-backlog` → 274/276.

**I did not re-run the full `just test` after re-blessing.** The argument that it would now be green
is: the only two failing tasks were the snapshot (now re-blessed, and `rebless-cards` re-ran that
exact test class to green) and the known `ai` flake, and every other module passed in the aborted
run. That is an argument, not an observation — if you want the whole gate green in one command,
re-run it.

**Mutation-proved.** The `Duration.EndOfYourNextTurn` branch in `CleanupPhaseManager.cleanupEndOfTurn`
was neutered to `false` (i.e. made to behave exactly like `EndOfTurn`) and
`just test-class EvilsThrallScenarioTest` re-run: **exactly** "a Villain with greater mana value: the
steal lasts until the end of your next turn" went red; the no-Villain case and the equal-mana-value
case both stayed green. Restored from a byte copy and re-run — all three green, and
`git diff --stat` on `CleanupPhaseManager.kt` is back to the 12-line addition.

## Things I'm unsure about — please look

- **The new duration is wired for floating effects only.** Control, P/T, keyword and type grants all
  land in `GameState.floatingEffects` and are covered. The separate granted-ability records
  (`grantedTriggeredAbilities`, `grantedStaticAbilities`, `grantedKeywordAbilities`,
  `globalGrantedTriggeredAbilities`, `grantedActivatedAbilities`) filter only on
  `!is Duration.EndOfTurn` at cleanup, so a future card authoring `EndOfYourNextTurn` *there* would
  get a permanent grant. That is exactly the state `Duration.UntilYourNextTurn` is already in for
  most of those lists, so I followed precedent rather than widening the diff — but I documented the
  hazard in the `Duration` KDoc and the language reference instead of fixing it. Say if you'd rather
  it were fixed.
- **`expiresAfterTurn` is a new field on `ActiveFloatingEffect`,** which is part of the serialized
  `GameState`. It defaults to `null`, so an older serialized state deserializes unchanged — but I did
  not run a cross-version replay to prove that.
- **"A Villain" is matched over `GameObjectFilter.Permanent`, not `Creature`** — the set's existing
  idiom (Yellowjacket, Doom Reigns Supreme). Every printed Villain is a creature today, so the two
  filters agree on the current pool.
- **Multiplayer.** The controller guard means the window ends on the *caster's* next turn regardless
  of how many opponents sit in between; the tests are two-player only, so that is an argument from
  the mechanism, not an observation.
- **Not done: manual playthrough in the web client, UX pass, e2e, AI-heuristic review.** No new
  decision type, no new `GameEvent`, no new keyword — the card produces the same
  `ControlChangedEvent` an Act of Treason does — but that is reasoning, not a check I ran.
- **The rendered duration string** is "until the end of your next turn"; nobody has looked at how
  the composed effect description reads in the real client.
