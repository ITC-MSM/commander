# u37 — Storm, Windrider (+ two combat/iteration primitives)

**Card.** Storm, Windrider [MSH 230] — {1}{G}{W}{W} 4/4 flier. Three clauses, three different
rules: `CantBeAttackedBy` (defender-side attack restriction), `CantBeBlockedBy` over a
battlefield-scoped `GroupFilter` (the "or block creatures you control" half — read from the
attacker's side, not a `CantBlock`), and a cast trigger whose payoff acts on the targets the
trigger captured.

**Primitive 1 — `CantBeAttackedBy(attackerFilter)`.** A *generalization*, not an addition: the old
`CantBeAttackedWithout(requiredKeyword, attackerFilter)` could only say "…**without** keyword", and
`GameObjectFilter.withoutKeyword` already expressed that. One filter-shaped static now covers both
polarities; the old type is deleted and Form of the Dragon + Teferi's Moat migrated onto it.
Engine: `CantBeAttackedWithoutDefenderRule` → `CantBeAttackedByDefenderRule`, plus a face-down
guard (CR 708.2) and the CR 506.3 / 508.1b scope fix below.

**Primitive 2 — trigger-time capture of a spell's targets.** `Triggers.youCastSpellTargeting(filter)`
now records the targets that satisfied its own `SpellCastPredicate.TargetsMatching` gate into
`TriggerContext.capturedEntityIds`, which the resolving ability sees as
`IterationSpace.TRIGGER_CAPTURED_COLLECTION` — the same engine-seeded slot a batched ETB payoff reads
(Kambal). Storm's payoff is then a plain
`ForEachInCollectionEffect(TRIGGER_CAPTURED_COLLECTION, GrantKeyword(FLYING, Self))`: no new effect
type, no new sealed variant. Gate and payoff are one computation (`TriggerMatcher.matchingCastTargets`),
so they can't drift, and the capture is a snapshot at trigger time — countering or retargeting the
spell in response to the trigger can't change which creatures gain flying (CR 113.7a).

**Engine fix behind the block half.** `CantBeBlockedByRule.hostScopedRestrictions` skipped the host
permanent whenever the host *was* the attacker, which silently exempted a creature whose own
battlefield-group clause names a group it belongs to (Storm is "a creature you control"). Host is
now included; the group filter's `excludeSelf` is honored for the "other creatures you control …"
wording. Only Wall Crawl (an enchantment) uses this shape today, so no existing card changes.

**Mutation-proved.** Each piece was stubbed on its own and the suite re-run, then restored:
`CantBeAttackedBy` disabled → 4/5 restriction tests + Storm's attack test red; the host-self skip
restored → *exactly* the two tests that cover it red, the other three group tests green; the
trigger-time capture returning `null` → 4/5 capture tests + all 3 Storm grant tests red (the "no
targets" one correctly stays green, and the combat tests stay green). Teferi's Moat's filter reduced
to a bare `Creature` → 3/4 of its new tests red, so its conjunction tests are discriminating; Form of
the Dragon's filter flipped to `withKeyword` → its (rewritten) attack test red.
**Finding from the first run:** the pre-existing `FormOfTheDragonTest > non-flying creature cannot
attack controller` stayed **green** under the stub — it asserted a life total, not the rejection.
It has since been rewritten to declare attackers and assert the rejection, and now fails under the
mutation.

**Gate.** `just test` (see the PR body for the counted totals). `just rebless-cards` → MSH.json
moves for Storm only; INV.json and SCG.json also move for the `CantBeAttackedWithout` →
`CantBeAttackedBy` structural rename (see below).
`just check-card-printing "Storm, Windrider"` ok; `just fix-backlog` → 272/276.
`docs/card-sdk-language-reference.md` updated (the new static, the spell-cast target capture, and
the `CantBeBlockedBy` group-scope note).

## Things I'm unsure about — please look

- **Two other sets' goldens moved, by design.** INV (Teferi's Moat) and SCG (Form of the Dragon)
  re-encode their static from `CantBeAttackedWithout` to `CantBeAttackedBy`. That is a *structural
  rename*, so it is not the "zero deletions" shape the unit brief describes as inherent movement —
  5 lines deleted across the two files (INV −3, SCG −2), replaced by the equivalent filter.
  Behaviour is preserved
  (same defending-player scan, same projected-keyword read, `withoutKeyword` → `CardPredicate.NotKeyword`
  which `PredicateEvaluator` answers off projected keywords). If a reviewer would rather keep the
  old type and add a parallel one, that is the call to reverse — I judged one primitive better than
  two overlapping ones, per `docs/sdk-design-principles.md`.
- **I changed Form of the Dragon's planeswalker behaviour.** The old rule mapped a planeswalker
  defender to its controller, so "creatures without flying can't attack you" also stopped attacks on
  that player's planeswalkers. Form of the Dragon's own 2014-02-01 ruling — printed in the card file —
  says the opposite, and CR 506.3 / 508.1b make a planeswalker a defender in its own right. The new rule returns early
  unless the chosen defender *is* the player. This is the one behaviour change to a card I did not
  otherwise own; I took it because I was redefining the primitive's meaning, but it is a fair thing
  to challenge.
- **`SpellCastPredicate.TargetsMatching` can match a creature *spell* on the stack.** Its helper
  (`TriggerMatcher.castTargetEntities`) includes `ChosenTarget.Spell`, and `IsCreature` is true of a
  creature card on the stack — so a counterspell aimed at a creature spell fires Storm's trigger
  (and Mockingbird's, and Iron Fist's — this is pre-existing, shared behaviour I did not touch).
  The capture is narrowed to battlefield permanents, so the payoff correctly does nothing; a test
  pins that end to end. Fixing the *trigger* is worth a separate unit; changing three cards'
  trigger conditions here would have been out of scope.
- **The face-down guard on the attack rule is tested by stamping `FaceDownComponent` directly**
  rather than by playing a morph, because no card with this static is morph-able. If the reviewer
  thinks that test proves too little, it can go — the guard itself matches the block rules' pattern.
- **Not tested: expiry of the granted flying.** `Duration.EndOfTurn` is shared engine machinery and
  I did not add a "next turn it's gone" assertion; the tests assert the grant lands, not that it
  lifts. (It is indirectly evidenced: the first version of these tests used a fixed
  `repeat(8) { bothPass() }`, which walked past cleanup and expired the grant — that is what made
  them fail. The tests now resolve only while the stack is non-empty.)
- **Combat tests need a decoy attacker.** With no legal attack at all the engine skips the
  declare-attackers step, so an "expect a rejection" assertion either never reaches the step or
  passes for the wrong reason. Every attack test here keeps an unrestricted ground creature on the
  attacking side and asserts `currentStep`/`activePlayer` before declaring. Worth knowing for the
  next combat-restriction unit.
- **Not tested: the `distinct()` in the capture** (a spell with two "target creature" instances
  aimed at the same creature). I could not set that up through the driver without fighting the
  target-legality checks, so the dedup is argued from CR 601.2c, not proven.
- **No manual playthrough in the web client, no UX pass, no e2e, no AI-heuristic review.**
  `ai/CardIntentAnalyzer` only special-cases `IterationSpace.Group`, and the payoff now uses the
  pre-existing `IterationSpace.Collection`, so nothing there changed at all.
