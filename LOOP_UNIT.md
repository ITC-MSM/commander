# u02 — Teamwork N (MSH)

Branch `loop-msh-u02`, stacked on `loop-msh-u01` (its two commits are the intended base).

## The primitive

**Teamwork N (CR 702.194)** — an optional additional cast cost ("tap any number of creatures you
control with total power N or more", 702.194a) plus a durable "cast using teamwork" fact (702.194b).

Authoring is one line: `card { teamwork(2) }`
(`mtg-sdk/src/main/kotlin/com/wingedsheep/sdk/dsl/mechanics/TeamworkDsl.kt`), the same shape as
`bargain()`.

### What it reuses

- **The kicker/bargain rail.** `KeywordAbility.OptionalAdditionalCost` with
  `declaredSlot = ChoiceSlot.TEAMWORK`. No new keyword-ability type, no new cast action, no new
  `actionType` (the variant is a `CastWithKicker` labelled "(Teamwork N)"), no new modal machinery.
  The slot is what makes "cast using teamwork" a different fact from "kicked"/"bargained".
- **`Conditions.TeamworkWasPaid`** is a one-line facade over `CastChoiceMade(ChoiceSlot.TEAMWORK)` —
  no new `Condition` subtype, exactly like `WasBargained`.
- **The crew selection.** "Tap any number of creatures you control with total power N or greater" is
  the crew payment. `rules-engine/.../mechanics/cost/VariablePermanentsCost.kt` is the single shared
  answer to "which permanents can pay / what do they measure", mirroring `CrewEnumerator`: untapped
  only (CR 701.26a), controlled by the payer, matched **and summed through projected state**, no
  summoning-sickness check (CR 302.6 is about the `{T}` symbol, not a tap paid as a cost). Both cast
  enumerators, the cast validator, `CostHandler`, `ActivateAbilityHandler.variableCostX` and the AI
  read it.
- **The crew/saddle client payload.** `TapForPowerCreatureData` / `TapForPowerCreatureInfo`, now also
  carried on `AdditionalCostData`/`AdditionalCostInfo` under `costType = "TapForTotalPower"`.
- **`AdditionalCostPayment.variableCostPermanents`**, the existing variable-count payment channel.

### What genuinely had to be added

- SDK: `Keyword.TEAMWORK`, `ChoiceSlot.TEAMWORK`, `Conditions.TeamworkWasPaid`,
  `Costs.additional.TapForTotalPower(n)`, `teamwork(n)` DSL helper.
- SDK cost atom: `PermanentCostAction.TAP`, `VariableCostMeasure.TOTAL_POWER`, and a
  `VariablePermanents.minMeasure` field — a floor on the *measure* rather than the count. Existing
  atom descriptions are unchanged (`minCount` defaults keep the old wording).
- Engine: `CostAtom.VariablePermanents` is now a **spell** additional cost, not only an
  activated-ability cost — validation + payment branches in `CastSpellHandler`, `paymentSatisfied`,
  `CostHandler.canPayAdditionalCost`, and a `TAP` payment branch in
  `CostHandler.payVariablePermanentsList` (SACRIFICE/EXILE branches on the cast path go through the
  existing sacrifice/zone-transition helpers).
- Enumerators: a `VariablePermanents` branch in `CastSpellEnumerator.enumerateKicker` and the
  matching one in `CastFromZoneEnumerator`, plus the "(Teamwork N)" label.
- AI: a `"TapForTotalPower"` branch in `Strategist.withAutomaticPayments` — biggest-power-first until
  the threshold is met, so the AI taps as few bodies as possible.
- Client: a `TapForTotalPower` case in the `costPayment` pipeline phase writing
  `variableCostPermanents`, `requiredTotalPower` / `powerByEntityId` on `TargetingState`, and a
  total-power confirm gate + "power 2/2" progress readout in `TargetingOverlay`. Powers come from the
  server; the client derives nothing.
- Docs: `docs/card-sdk-language-reference.md` (Teamwork N section, the cost-atom axes, the
  `Costs.additional.TapForTotalPower` bullet, the `TeamworkWasPaid` condition), plus the
  `## Teamwork N` section of `backlog/sets/marvel-super-heroes/mechanics.md` marked shipped.

## The cards

- **Helicarrier Strike** [15] `{W}` Instant — 2 damage to target attacking or blocking creature, 4
  instead if cast using teamwork. Composes `teamwork(2)` + `Effects.DealDamage` fed a
  `DynamicAmount.Conditional(Conditions.TeamworkWasPaid, 4, 2)`. Deliberately one damage event rather
  than 2+2, so prevention shields and damage triggers see what the printed card does.
- **Repulsor Blast** [150] `{3}{R}` Sorcery — 5 damage to target creature; if cast using teamwork,
  also 2 to that creature's controller. Composes `teamwork(2)` + `Effects.DealDamage` +
  `ConditionalEffect(TeamworkWasPaid, DealDamage(2, EffectTarget.TargetController))`.
- **Team Tactics** [155] `{1}{R}` Instant — target creature gains double strike; also trample if cast
  using teamwork. Composes `teamwork(1)` + two `Effects.GrantKeyword` grants, the second under a
  `ConditionalEffect`.

All three are MSH-only printings (checked on Scryfall), so MSH is canonical for each.

## Tests

- `TeamworkMechanicScenarioTest` — multi-creature payment, single-creature payment, declining the
  cost, an unmet threshold, an already-tapped creature, an opponent's creature, a lord's bonus
  counting toward the threshold (projected power), summoning sickness not mattering, the flag
  surviving onto a resolving permanent, the intervening-if not firing without teamwork,
  teamwork-vs-kicked separation, declaring teamwork on a card that lacks it, and the advertised
  legal action (candidates, threshold, affordability, label).
- `HelicarrierStrikeScenarioTest`, `RepulsorBlastScenarioTest`, `TeamTacticsScenarioTest` — one file
  per card, each asserting both branches.
- New fixture: `ScenarioTestBase.castSpellWithTeamwork(...)`.

## Gate

`just test` — see the final report for the result.

## Things I'm unsure about / would like reviewed

- **The web client was not type-checked.** `web-client/node_modules` is absent and there is no
  network in this container, so `just client-typecheck` cannot run. The client changes are small and
  reviewed by eye, but they are unverified by a compiler.
- **No manual playthrough, no e2e.** Nobody has clicked a teamwork cast in the real client. The
  cost-payment phase reuses the existing on-battlefield targeting overlay with a new confirm gate;
  the "power 2/2" readout and the greyed-out unaffordable variant are untested visually.
- **Cost-vs-target announcement order — resolved on review; there is no deviation.** CR 702.194a
  says teamwork payment follows the additional-cost rules in **601.2b and 601.2f–h** ("601.2b–f",
  written here originally and in the first commit message, is not a range the rule uses). The
  declaration belongs at 601.2b, *before* targets at 601.2c, and CR 702.194c explicitly requires it
  there. The engine already does that: the cast variant carrying `declaredCostSlot` is chosen before
  any pipeline phase, cost and target validation both run against the pre-payment state, and the
  taps are applied only after validation in one atomic action. Only the *client's* collection order
  (cost widget before target picker) differs, with no rules consequence.
- **`CostAtom.VariablePermanents` on the cast path now handles all three actions.** Only `TAP` has a
  card and a test; the `SACRIFICE` and `EXILE` cast-path branches exist because the `when` is
  exhaustive and reuse the existing helpers, but nothing exercises them. Same for the third
  unreachable branch, `TAP` in `CostHandler.payVariablePermanentsList` — no printed card pays a TAP
  `VariablePermanents` cost from an activated ability yet. All three kept, reviewed and accepted.
- **`selectionCount` for the teamwork atom is 0** (`minCount = 0`), since the count is free. Nothing
  in the cast path reads it, but it is a slightly odd value for a cost that always selects at least
  one permanent in practice.
- **mtgish bridge: declined deliberately.** The corpus (`mtgish-tooling/data/mtgish.lines.json`) has
  no `Teamwork` keyword rule tag — the only teamwork-related tag is
  `WhenAPermanentBecomesTappedToPayATeamworkCost` on Agent Maria Hill, which belongs to a later unit.
  There is no IR tag for this keyword to map, so no bridge/emitter entry was added.
