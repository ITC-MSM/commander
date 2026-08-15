# loop-msh-u32 — Red Guardian, Super-Soldier + active per-turn "dealt damage" predicate

Base branch: **`loop-msh-u23`** (not `main`). Stacked. Since u23 was itself rebased onto
`origin/main`, `main` *is* now an ancestor, but the u23 commits below this branch are not upstream
yet — this still waits for u23 to land before it can be opened on its own.

## The primitive

- **`StatePredicate.HasDealtDamage` grew a window parameter** — `data object` → `data class
  HasDealtDamage(val thisTurnOnly: Boolean = false)`
  (`mtg-sdk/.../scripting/predicates/StatePredicate.kt`). Default `false` keeps the existing
  lifetime reading; `true` is the new "dealt damage **this turn**".
- **`HasDealtDamageComponent` grew a turn stamp** — `data object` → `data class
  HasDealtDamageComponent(val lastDealtDamageTurn: Int)`
  (`rules-engine/.../state/components/battlefield/BattlefieldComponents.kt`). Presence answers the
  lifetime window; `lastDealtDamageTurn == state.turnNumber` answers the per-turn one. No default on
  the parameter, so no stamp site can forget to record the turn.
- **Why one marker, not a parallel `DealtDamageThisTurnComponent`** (which is what the MSH triage note
  proposed): a damage path physically cannot record one window without recording the other, which is
  the u23 failure mode. It also needs no `CleanupPhaseManager` wiring — a stale stamp stops matching
  on its own once the turn number moves.
- **Shared read**: `rules-engine/.../handlers/predicates/HasDealtDamagePredicate.kt`, mirroring
  u23's `ReceivedCounterThisTurnPredicate.kt`.
- **Dispatch sites wired**: `PredicateEvaluator`, `AffectsFilterResolver`, `TriggerMatcher` and
  `BeginningPhaseManager` — all four answer exactly, none falls open. The untap helper takes only a
  container, but it doesn't need `state.turnNumber`: every caller runs during an untap step, the first
  step of the turn (CR 500.1 / 501.1) with no priority (CR 500.3) and with `turnNumber` already
  incremented, so the per-turn window is `false` for every permanent and the lifetime window is the
  marker's presence.
- **Naming-trap fix (deliberate, in scope)**: `ObjectFilter`/`TargetFilter.dealtDamageThisTurn()` used
  to mean the **passive** `WasDealtDamageThisTurn`. It is renamed `.wasDealtDamageThisTurn()`, and the
  active predicate takes the *new* name `.hasDealtDamageThisTurn()` — the short name is retired, not
  reused, so an un-rebased branch still saying `.dealtDamageThisTurn()` fails to compile at the call
  site instead of silently flipping to the opposite set of permanents. All 5 existing call sites
  updated (Crushing Pain, Qutrub Forayer, Rooftop Assassin, Stingblade Assassin), plus the mtgish
  emitter and its test.
- **Not a bug fix after all**: `CombatDamageManager.applyDamageReflection` (Harsh Justice) now stamps
  `HasDealtDamageComponent` too, but tracing the control flow shows its only caller,
  `applyDamageToPlayer`, already stamped the same creature under the same guard on the way in — so the
  line changes no current behaviour. Kept as a function-local invariant ("every path that emits a
  `DamageDealtEvent` stamps its source"), with the comment saying so plainly.

## Damage paths verified (each stamps the marker with the current turn)

| Path | Where | Covered by |
|---|---|---|
| All noncombat damage (effect executors, fight, divided, per-entity, exile-from-top, combat continuation resumer) | `DamageUtils.dealDamageToTarget`, function-level below every recipient branch | test + code read |
| Combat damage to a player | `CombatDamageManager.applyDamageToPlayer` | test |
| Combat damage to a creature (incl. wither) | `CombatDamageManager.dealFinalDamage` creature branch | test |
| Redirected combat damage to a player | `dealFinalDamage` player branch | code read only |
| Combat damage to planeswalker / battle | `removeCountersForDamage` | test |
| Combat damage reflection (Harsh Justice) | `applyDamageReflection` (redundant with the caller's stamp) | test |

Enumeration method: every `DamageDealtEvent(` emission site and every `with(DamageComponent(` site in
non-test source. There are exactly two damage implementations — `DamageUtils.dealDamageToTarget` and
`CombatDamageManager` — so the set is closed.

## The card

`Red Guardian, Super-Soldier` (MSH #34, {2}{W} 2/2 Legendary Human Soldier Villain, Flash). ETB
destroys target creature an opponent controls that dealt damage this turn. Uses
`TargetFilter.Creature.hasDealtDamageThisTurn().opponentControls()` + `Effects.Destroy(t)` — no
card-specific engine code. MSH is the only printing, so canonical placement is here.

## Tests

- `rules-engine/.../predicates/HasDealtDamagePredicateTest.kt` — the primitive: window logic against
  the shared helper (no marker / this-turn marker / earlier-turn marker / passive marker), plus the
  recording paths played out in real games (combat→player, combat→creature, combat→planeswalker
  loyalty, a Harsh Justice reflection, activated-ability noncombat, spell damage stamps nobody, turn
  boundary, zone change clears).
- `rules-engine/.../scenarios/RedGuardianSuperSoldierScenarioTest.kt` — the card: happy path, the
  passive-vs-active discrimination (Shocked creature is *not* a target), the per-turn-vs-lifetime
  discrimination (dealt damage last turn is *not* a target), the controller half, and noncombat damage.
  Each negative case asserts the *decision*: the stack drained with no target choice ever offered
  (CR 603.3d). Survival alone can't fail — `resolveStack()` halts on a pending decision, so a
  wrongly-matching predicate would leave the victim alive with the trigger still waiting.
- `AffectsFilterResolverStatePredicateTest` — projection dispatch, both windows.
- `TargetRecoveryTest` — emitter renders both voices.

## Gate

> **Invalidated by a rebase.** The result below was measured against the *old*, pre-squash
> `loop-msh-u23` tip (`3632d6b2ce`), on a much older `origin/main`. This branch has since been
> replayed onto the squashed u23 (`8e3c8bdfd5`), which carries ~15.8k changed files from upstream
> — including the per-era `mtg-sets/` split that moved this card to `mtg-sets/2026/`. **Nothing
> below has been re-run on the new base.** Re-gate before reporting green.

`just test` — **passed** (`BUILD SUCCESSFUL`, zero failures). `:rules-engine:test` ran in full on the
final pass; other modules were `UP-TO-DATE` from earlier green runs. `just rebless-cards` moved only
`MSH.json` (+63/-0, the new card only). `just coverage-fixtures --rebless` moved two golden lines, both
the passive rename. `just check-card-printing` clean. `just fix-backlog` → 255 / 276.

Took three gate runs: a stale Kotlin daemon holding this worktree's cache, then the expected MSH
snapshot golden, then four failures in my own new tests — all harness misuse (blocker step not
advanced, a cross-turn test decking a player on an empty library, priority not handed back after the
opponent acted), none an engine bug.

## Things I'm unsure about / did not verify

- Redirected combat damage to a player (Glarecaster-style) is verified by reading the code, **not** by
  a test. Planeswalker damage and the reflection path now have tests.
- The active mtgish IR tag `DealtDamageThisTurn` is a **guess**. The local corpus
  (`mtgish-tooling/data/mtgish.lines.json`) is a truncated 1 MiB sample predating MSH and contains only
  passive spellings, so nothing confirms mtgish emits the bare tag for the active voice. Fail-safe in
  both directions — if the guess is wrong the render branch simply never fires — but it is unverified.
- `HasDealtDamageComponent(lastDealtDamageTurn)` has **no default**, on purpose: every stamp site must
  name the turn. The cost is that a live game persisted before this change (Redis, `PersistentGameSession`)
  fails to decode on restore with a `MissingFieldException`. TTL'd live state, no migration layer in the
  repo — flagged rather than defaulted, because `= 0` would let a future stamp site silently record
  "never, this turn".
- No manual playthrough in the web client, no UX pass from both seats, no e2e. A playtest scenario JSON
  is shipped at `manual-scenarios/sets/msh/loop-msh-u32-red-guardian.json` but nobody has run it.
- I widened five mtgish emitter "can't render alongside" lists from `"WasDealtDamageThisTurn"` to
  `"DealtDamageThisTurn"` (a substring, so it catches both voices). This only ever declines *more*,
  never less, but it is a behaviour change to the generator's shortcut branches.
- The `TriggerMatcher` dispatch site is exercised by no test: no card uses the predicate as a trigger
  filter yet, so the first card that does will be its first coverage.
