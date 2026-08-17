# u20 — Kang the Conqueror (MSH)

**Card.** `Kang the Conqueror` {2}{U}{U} 4/5 Legendary Creature — Human Villain. Flying, plus
`Power-up — {5}{U}{U}{U}: Put a +1/+1 counter on Kang. Take an extra turn after this one. During
that turn, power-up abilities can't be activated.` — `Effects.Composite(AddCounters, TakeExtraTurn)`
under an `isPowerUp = true` activated ability, so the once-per-object limit and the pip-wise self
discount ({5}{U}{U}{U} → {3}{U} the turn he lands) come for free.

**Primitive.** No new type. `TakeExtraTurnEffect` gained a third rider parameter,
`powerUpAbilitiesCantBeActivated`, alongside the existing `loseAtEndStep` (Last Chance). The
executor stamps the extra turn's number (`turnNumber + 1`) into a new
`GameState.powerUpRestrictedTurns`, and `CastPermissionUtils.isPowerUpActivationRestricted` is
consulted by `ActivateAbilityHandler.validate` plus all four ability enumerators (the fourth,
`CommandZoneAbilityEnumerator`, was added in the review-correction pass along with a test).

**Planner triage was accurate this time.** "Power-up + extra turn + a turn-scoped activation
restriction" is exactly what the Oracle text says (verified against Scryfall), `powerUp` and
`TakeExtraTurn` both already existed, and the turn-scoped restriction was genuinely the new part.

**Gate.** `just test` — see the PR body for the recorded result. `just rebless-cards` (only Kang
moved), `just check-card-printing "Kang the Conqueror"` and the backlog tick + `just fix-backlog`
(269/276) also ran.

## For the reviewer — things worth a second opinion

- **Why a rider parameter and not a standalone effect.** "During *that* turn" refers to the turn
  `TakeExtraTurnEffect` creates. If `PreventExtraTurns` (Ugin's Nexus) stops the extra turn, there
  is no turn to bind and the lockout must not apply — a sibling effect sequenced after it in the
  `Composite` could not see that, and would lock out a turn that was never granted. There is a test
  for exactly this. The counter-argument is that it is another boolean parameter; say so if you'd
  rather see a general "rider effect on the extra turn" shape instead.
- **Why a `GameState` field rather than a player component.** The three existing restriction
  vehicles all fail one of the requirements: `PlayersCantActivateAbilities` is a static read off a
  battlefield permanent (dies with Kang), `CantActivateLoyaltyAbilitiesComponent` is per-player and
  its `PlayerEffectRemoval` durations all *start now* (which would wrongly lock the rest of the
  current turn), and a delayed trigger would leave an unlocked priority window in the extra turn's
  upkeep before it resolved. Turn-number stamping is the only one that is global, future-scoped and
  source-independent. `damageCantBePreventedThisTurn` is the nearest existing precedent.
- **Extra-turn ordering (CR 500.7) is approximated.** The engine models extra turns as opponents
  skipping, with no queue, so `turnNumber + 1` is "the next turn to begin". CR 500.7's "the most
  recently created turn will be taken first" means that is correct when Kang's is the last extra
  turn created — but if Kang resolves and *then* a Time Warp resolves in the same turn, Time Warp's
  turn is taken first and the lockout lands on the wrong one. Not fixable without an extra-turn
  queue; out of scope, and untested.
- **`u18`'s `ai/EffectWalker` lesson checked and does not apply.** `TakeExtraTurnEffect` has no
  consumer in `ai/`, `gym/` or `game-server/` (verified by grep); nothing is being wrapped, only a
  defaulted parameter appended, so positional constructor/facade callers are unaffected.
- **`u29`'s handler/enumerator split explicitly closed.** The guard is in
  `ActivateAbilityHandler.validate` *and* in `ActivatedAbilityEnumerator` (own-permanent and
  any-player-may paths), `ManaAbilityEnumerator` and `ZoneActivatedAbilityEnumerator`. The last two
  are dead code today — no printed power-up ability is a mana ability or activates off the
  battlefield — and are commented as such. Push back if you'd rather not carry speculative guards.
- **`u21` (Wonder Man) is left room, not fought.** Wonder Man's "each power-up ability can be
  activated an additional time" is a relaxation of `ActivationRestriction.Once`, tracked per-object
  in `AbilityActivatedThisTurnComponent`/the `Once` check; this lockout is an independent hard gate
  that runs before restrictions are evaluated. They compose the way the rules demand: an extra
  activation still can't be used during Kang's turn. No `Once` machinery was touched.
- **No `GameEvent` is emitted for the lockout.** `TakeExtraTurnExecutor` already writes the
  `SkipNextTurnComponent`s silently, and `DamageCantBePreventedThisTurnExecutor` sets its `GameState`
  flag the same way, so this follows precedent — but it does sit against AGENTS.md's "events, not
  silent mutations". Flag it if a turn-restriction event should exist.
- **No multiplayer test.** The restriction is global by construction, but every test is two-player.
