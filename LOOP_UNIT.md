# loop-msh-u21 — Wonder Man, Hollywood Hero

**Card.** Wonder Man, Hollywood Hero (MSH #160), {3}{R}{R} 4/4 Legendary Creature — Human Performer
Hero: flying, "Each power-up ability of permanents you control can be activated an additional time",
and Power-up — {5}{R}{R}: two +1/+1 counters on himself.

**Primitive.** No new type. The existing exhaust-waiver static was *generalized*:
`IgnoreExhaustActivationLimit` → `ExtraOnceOnlyActivations(kind, extraActivations, condition)`, and
its engine resolver `ExhaustActivationWaiver` → `OnceOnlyActivationAllowance`. `kind` picks exhaust
(CR 702.177) vs power-up (CR 702.193) — they desugar to the *same* `ActivationRestriction.Once`, so
without that axis a power-up permission would re-arm every exhaust ability on the board.
`extraActivations` picks waive (`null`, Elvish Refueler) vs raise-by-N (`1`, Wonder Man), summed
across the battlefield. `AbilityActivatedEverComponent` gained the per-object activation *count*
raise-by-N needs (it was a `Set` before); `activationCount` falls back to set membership so a state
serialized before the field still reports ≥1.

**Composition with u20's lockout.** Kang's `powerUpRestrictedTurns` gate is checked in
`ActivateAbilityHandler.validate` and each enumerator *before* any `ActivationRestriction`, so it
still wins — a raised ceiling has nothing to raise (CR 101.2, verified locally). Two tests in
`ExtraOnceOnlyActivationsScenarioTest` pin it: during a locked turn the re-armed ability is withheld
by the enumerator and rejected by the handler with the lockout's message (not the spent-`Once`
message), and after the locked turn the unspent extra activation is still available.

**Gate.** `just test` — **passed**, verified from `build/test-results` (zero failures across all ten
modules; `ExtraOnceOnlyActivationsScenarioTest` 12/12, `WonderManHollywoodHeroScenarioTest` 4/4,
`ElvishRefuelerScenarioTest` 4/4, `ExhaustKeywordScenarioTest` 3/3, `PowerUpKeywordScenarioTest`
13/13, `KangTheConquerorScenarioTest` 4/4). Run twice: the first stopped at the expected snapshot
drift before `:rules-engine:test` was reached, so the green above is the post-rebless run.
`just rebless-cards` — `MSH.json` gains only Wonder Man (zero deletions), `DFT.json` changes one
line (Elvish Refueler's `"type"`). `just check-card-printing "Wonder Man, Hollywood Hero"` — ok,
MSH is the only printing. `just fix-backlog` — MSH now 270/276.

**Things worth a reviewer's eye**

- The rename changes `@SerialName("IgnoreExhaustActivationLimit")` → `"ExtraOnceOnlyActivations"`, so
  Elvish Refueler moves in `DFT.json` and any *persisted* game state holding the old name would no
  longer deserialize. I judged that acceptable; say so if this repo cares about save compatibility.
- `CastPermissionUtils.checkActivationRestriction` and its `ActivateAbilityHandler` twin now take
  `ability: ActivatedAbility?` instead of `isExhaustAbility: Boolean` — deliberate, so a second
  keyword doesn't need a second boolean threaded through five call sites. All five enumerator call
  sites plus the handler pass it; the `null` default still gives the restrictive answer.
- `ManaSolver`'s inlined `Once` branch now calls the same helper, so auto-tap agrees. `ManaSolver`'s
  *permission* blind spot (it filters on `isManaAbility` with no lockout check) is pre-existing and
  untouched — no printed power-up or exhaust ability is a mana ability.
- Elvish Refueler's static-ability `description` string is unchanged for the `null` case by
  construction, but I have not eyeballed the reblessed `DFT.json` beyond that claim.
- `kind` defaults to `EXHAUST`. That default is arbitrary on a two-kind type; I kept it so the
  reblessed `DFT.json` diff stays a single type-name line (kotlinx omits defaults), and both call
  sites pass `kind` explicitly anyway. Making it a required parameter is a one-line change if you
  think the explicitness is worth the extra snapshot churn.
- Not done: no manual playthrough, no e2e, no UX/AI-heuristic review.

## Review corrections (post-review commit)

- Oracle text now reproduces Scryfall verbatim, joke and all: `only . . . once?`, not `only once.`
  (MSH.json reblessed — one line, Wonder Man only).
- `ability` is a **required** parameter of both `CastPermissionUtils.checkActivationRestriction` and
  its `ActivateAbilityHandler` twin; both dead `null` fallbacks deleted. A forgetful call site is now
  a compile error rather than a silently disabled permission.
- `ExtraOnceOnlyActivations.kind` no longer defaults (one added line in `DFT.json`), and an `init`
  block requires `extraActivations == null || >= 1`.
- `extraActivationsFor` skips the *printed* statics of a face-down granter (CR 708.2/708.2a); granted
  statics still apply. The base-vs-projected-controller and `Duration`-gate gaps are documented in
  place as house-wide and deliberately left alone.
- Three tests added: no end-of-turn refresh of a spent allowance, CR 400.7 fresh allowance after a
  bounce-and-recast, and waive-beats-counted for the same `kind`. Each was mutation-checked to fail
  alone under a targeted break.
- `activationCount`'s `abilityIds` fallback is **kept**: live `GameState` is Redis-persisted whole,
  so it is reached by any game in flight across the deploy. Comment now says so.
