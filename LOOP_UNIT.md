# u05 — tap reason + Agent Maria Hill

Branch `loop-msh-u05`, built locally on `loop-msh-u04`. u01–u03 have all merged upstream (u03 as
PR #1750), so the only extra commits in `git diff origin/main...HEAD` are u04's four; **review this
unit against `loop-msh-u04...HEAD`**.

Not a stacked PR: once u04 lands upstream, this branch is rebased onto `origin/main` and opened on
its own. Being built on u04 is a local convenience, not a merge order — and note that a rebase
invalidates this unit's gate, so re-run it on the new base before reporting green.

## The primitive

`TapReason` — `mtg-sdk/src/main/kotlin/com/wingedsheep/sdk/scripting/TapReason.kt`. A serializable
enum, two members: `UNSPECIFIED` (default) and `TEAMWORK`. `TapReason.forChoiceSlot(slot)` maps a
declared cast-choice slot to a cause; only `ChoiceSlot.TEAMWORK` maps to anything.

Threaded through:

- `TappedEvent.reason` (`rules-engine/.../core/GameEvent.kt`), defaulted, so serialized/replayed
  events from before the field decode unchanged.
- `tap(state, entityId, tappedById, reason)` (`rules-engine/.../core/TapHelpers.kt`) — the tap atom;
  the reason defaults to `UNSPECIFIED`.
- `EventPattern.TapEvent.reason: TapReason?` (`mtg-sdk/.../scripting/EventPattern.kt`) — null means
  "any cause", so every existing tap trigger is unchanged. Rendered into the pattern description.
- Matching: `TriggerMatcher` (per-event), `TriggerDetector.detectTapBatchTriggers` (batch — narrows
  the batch by reason the same way it already narrows by tapper), `AttachmentTriggerDetector`
  (ATTACHED binding).
- DSL: `Triggers.becomesTapped(binding, filter, reason)` gained the parameter;
  `Triggers.BecomesTappedForTeamwork` is the SELF facade the card uses.

## Classified vs unspecified tap sites

**Classified: teamwork only.** `CastSpellHandler`'s `CostAtom.VariablePermanents` payment branch
stamps `TapReason.forChoiceSlot(action.declaredCostSlot)`, and only on the additional cost that the
*declared* optional ability contributed (`declaredSlotAdditionalCost`), so a card's own printed tap
cost can't be relabelled by an unrelated declaration.

**Deliberately unspecified: everything else** — attacking, crew, saddle, convoke, mana abilities, a
`{T}` activation cost, "tap target permanent" effects, and the activated-ability `VariablePermanents`
TAP payer in `CostHandler`. Rationale: the cause is named by the mechanic that declared the cost, and
an ability cost has no declared slot. Under-claiming makes a reading card stay silent; over-claiming
makes it fire wrongly. I did not classify attack/crew even though each has a single chokepoint — no
card reads them, and the enum is documented with the recipe for adding one.

I acted on the previous units' suggestion: both tap sites for the atom now go through one chokepoint,
`VariablePermanentsCost.tapAll(state, chosen, reason)`. Both `TAP-REASON HOOK` markers are gone.

**One behavioural side effect to check in review:** folding `CostHandler.payVariablePermanentsList`'s
TAP branch onto `tapAll` moved its per-permanent validation into a pass over the *pre-payment* state.
The old interleaved loop rejected a duplicated id incidentally (second pass saw it already tapped);
I replaced that with an explicit `toPay.distinct()` guard, mirroring the one `CastSpellHandler.validate`
already has. That branch is unreached by any printed card today.

## The card

`mtg-sets/.../definitions/msh/cards/AgentMariaHill.kt` — {W} 2/1 Legendary Creature — Human Spy Hero,
MSH #2, verified against Scryfall (name, cost, type line, oracle text, P/T, rarity, collector number,
artist, flavor, image URI HTTP 200). No rulings on Scryfall. Composes existing primitives only:
`Triggers.BecomesTappedForTeamwork` + `Effects.Composite(Effects.AddCounters(+1/+1, 1, Self),
Effects.DrawCards(1))`. Canonical printing is MSH (first printing).

## Tests

- `AgentMariaHillScenarioTest` — fires on a teamwork tap (counter + draw, projected 3/2); silent when
  tapped by attacking, by crewing a Vehicle, and when a teamwork cost is paid by another creature; one
  `getLegalActions` assertion that the teamwork cast variant is still advertised and prices her at
  power 2 as an eligible payer.
- `TapReasonScenarioTest` — the primitive itself: teamwork tap carries `TEAMWORK` (and its
  `tappedById` is unchanged); the mana-payment land taps of that same cast are `UNSPECIFIED`; a plain
  cast claims no teamwork anywhere; attack tap and crew tap are `UNSPECIFIED`; a cause-agnostic
  `becomes tapped` trigger (Interface Ace) still fires on a teamwork tap; pattern description and
  default; `TappedEvent` serialization round-trip plus legacy JSON without the field.

## Docs / backlog

- `docs/card-sdk-language-reference.md`: `becomesTapped` entry gained the `reason` parameter, a new
  `BecomesTappedForTeamwork` entry explains `TapReason` and the under-claiming policy, and the
  Teamwork N mechanic block gained a "payer-side payoff" paragraph.
- `mtgish-tooling` bridge: one comment on `WhenAPermanentBecomesTapped` saying it renders the
  cause-agnostic trigger only and must not be used to draft the teamwork wording. No new capability
  registered — the IR has no teamwork tag and this is a predicate on an existing trigger, not a new
  primitive the emitter can map.
- `backlog/sets/marvel-super-heroes/cards.md`: Agent Maria Hill ticked, header resynced via
  `just fix-backlog`.
- `backlog/sets/marvel-super-heroes/mechanics.md`: Teamwork N marked fully shipped (13/13), point 5
  added for the tap cause. No other section touched.

## Gate

`just test` — see the PR body for the result. `just rebless-cards` moved only Agent Maria Hill in
`mtg-sets/src/test/resources/snapshots/cards/MSH.json`; `just check-card-printing "Agent Maria Hill"`
clean.

## Things I am unsure about

- Whether the reviewer would rather see `TapReason` live next to `ChoiceSlot` (where I put it) or in
  `sdk/scripting/events/` with the other event vocabulary.
- Whether `tapAll` is thick enough to justify existing, given each caller still validates separately.
  I kept it because it is what makes "one tap site per atom" true and is where the cause is documented.
- The `additionalCost == declaredSlotAdditionalCost` identity check is structural equality on a data
  class. It is exact for every printed card, but two structurally identical additional costs on one
  card (one printed, one from the declared optional ability) would both be stamped.
- No client work: `ClientEvent.PermanentTapped` does not carry the reason. Nothing in the UI needs it
  today (the trigger is server-detected and shown as a stack object), but a future "why did this tap?"
  affordance would need it plumbed.
- Not done: no manual playthrough in the web client, no two-seat UX pass, no e2e test.
