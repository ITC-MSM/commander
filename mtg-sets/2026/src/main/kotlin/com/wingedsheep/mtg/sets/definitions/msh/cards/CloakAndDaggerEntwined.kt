package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.EffectChoice
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.RevealHandEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Cloak and Dagger, Entwined — Marvel Super Heroes #211 (rare)
 * {1}{W}{B} · Legendary Creature — Human Hero · 2/2
 *
 * Deathtouch, lifelink
 * When Cloak and Dagger enter, choose target opponent and up to one target creature they control.
 * They reveal their hand. You may exile a nonland card from their hand or the chosen creature
 * until Cloak and Dagger leave the battlefield.
 *
 * Modeling notes:
 *  - **Two targets, one exile.** `target opponent` is target 0 (it names the player whose hand is
 *    revealed and exiled from); `up to one target creature they control` is target 1, an optional
 *    [TargetCreature] over `CreatureOpponentControls`. Only one of the two things is ever exiled.
 *
 *    The *filter* on that second slot is the **two-player rendering** this repo already uses for
 *    relational target slots — the approximation Demonic Junker, Kitesail Larcenist and Unstable
 *    Glyphbridge document. (The target *label* keeps the printed "they control": the divergence is
 *    in which creatures are legal, not in what the prompt should say.) `ControllerPredicate`
 *    already has the faithful predicate, `ControlledByTargetPlayer`, and `TargetValidator` already
 *    resolves it against sibling choices — it pulls the chosen player out of `allTargets` and
 *    checks the creature against them. What's missing is the *enumeration* half: `TargetFinder`'s
 *    `findLegalTargets`/`findObjectTargets` take one `TargetRequirement` with no view of the other
 *    requirements' choices, so `PredicateEvaluator` sees `context.targetPlayerId == null` and
 *    resolves the predicate to `false` for every candidate — an empty legal-target list, i.e. a
 *    creature slot that could never be picked at all. So the gap is narrow and concrete: thread the
 *    already-chosen targets into `findLegalTargets` the way `TargetValidator` already does.
 *
 *    With one opponent the two readings coincide exactly. In a **multiplayer pod** this slot is
 *    laxer than the printed text (any opponent's creature, not only the chosen opponent's) — and
 *    that is not hypothetical: `backlog/multiplayer.md` is an active Free-for-All plan (CR 806) and
 *    the AI harness already runs three-seat games, so this card is on the list of things a
 *    multiplayer pass has to revisit.
 *  - **The either/or is an [Effects.ChooseAction]**, wrapped in [MayEffect] for the "You may".
 *    The two branches exile from different zones, which is the whole point of this card:
 *      * the hand branch is the standard gather → select → move pipeline with
 *        `linkToSource = true`, which is what puts the card into Cloak and Dagger's linked-exile
 *        pile (`ExileUntilLeaves` only accepts battlefield permanents and graveyard cards, so it
 *        can't reach a hand);
 *      * the creature branch is plain [Effects.ExileUntilLeaves] on the chosen creature.
 *    Neither branch carries a [com.wingedsheep.sdk.scripting.effects.FeasibilityCheck], and
 *    neither *can* today. `ChooseActionEffectExecutor` evaluates a check as
 *    `isFeasible(state, choosingPlayerId, check)` — no effect context — and the two available
 *    variants (`ControlsPermanentMatching`, `HasCardsInZone`) both read the **choosing** player's
 *    own zones and battlefield. The hand branch's condition is about the *targeted opponent's*
 *    hand, which needs a player-scoped check; the creature branch's condition is "was the optional
 *    target actually chosen?", which needs a **context-target-presence** check plus threading the
 *    `EffectContext` into `isFeasible` (three call sites: `ChooseActionEffectExecutor`,
 *    `ReflexiveTriggerEffectExecutor`, `GatedEffectExecutor`). Both are `add-feature` work, not a
 *    card-level fix. Until then a branch with nothing to exile — empty opponent hand, or no
 *    creature chosen — is still offered and resolves as a no-op, rather than being hidden the way
 *    Bullseye, Death Dealer's two branches are. `SelectFromCollectionExecutor`'s `ChooseExactly`
 *    over an empty eligible set auto-selects nothing rather than hanging, so the no-op is safe;
 *    both dead branches are covered in the scenario test. Whoever adds either check should come
 *    back here.
 *  - **The return is [Effects.ReturnLinkedExileToZoneExiledFrom]** — CR 610.3's "returns the object
 *    to its previous zone". The hand card goes back to its owner's hand and the creature back onto
 *    the battlefield (as a new object per CR 400.7, under its owner's control per CR 610.3c) from
 *    the same one leaves-the-battlefield trigger, because the destination is resolved per card
 *    from the zone each was exiled from.
 *  - "They reveal their hand" happens unconditionally, before the may-decision — you get to look
 *    before choosing whether and what to exile.
 */
val CloakAndDaggerEntwined = card("Cloak and Dagger, Entwined") {
    manaCost = "{1}{W}{B}"
    colorIdentity = "WB"
    typeLine = "Legendary Creature — Human Hero"
    power = 2
    toughness = 2
    oracleText = "Deathtouch, lifelink\n" +
        "When Cloak and Dagger enter, choose target opponent and up to one target creature they " +
        "control. They reveal their hand. You may exile a nonland card from their hand or the " +
        "chosen creature until Cloak and Dagger leave the battlefield."

    keywords(Keyword.DEATHTOUCH, Keyword.LIFELINK)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val opponent = target("target opponent", Targets.Opponent)
        val creature = target(
            // Printed wording. The *filter* is the two-player approximation (see the KDoc); the
            // label is what the targeting prompt shows, so it stays faithful to the card.
            "up to one target creature they control",
            TargetCreature(optional = true, filter = TargetFilter.CreatureOpponentControls),
        )
        effect = Effects.Composite(
            RevealHandEffect(opponent),
            MayEffect(
                Effects.ChooseAction(
                    listOf(
                        EffectChoice(
                            label = "Exile a nonland card from their hand",
                            effect = Effects.Composite(
                                GatherCardsEffect(
                                    source = CardSource.FromZone(
                                        Zone.HAND,
                                        Player.ContextPlayer(0),
                                        GameObjectFilter.Nonland,
                                    ),
                                    storeAs = "cloakDaggerHand",
                                ),
                                SelectFromCollectionEffect(
                                    from = "cloakDaggerHand",
                                    selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(1)),
                                    chooser = Chooser.Controller,
                                    storeSelected = "cloakDaggerExiled",
                                    prompt = "Choose a nonland card to exile",
                                ),
                                MoveCollectionEffect(
                                    from = "cloakDaggerExiled",
                                    destination = CardDestination.ToZone(
                                        Zone.EXILE,
                                        Player.ContextPlayer(0),
                                    ),
                                    linkToSource = true,
                                ),
                            ),
                        ),
                        EffectChoice(
                            label = "Exile the chosen creature",
                            effect = Effects.ExileUntilLeaves(creature),
                        ),
                    ),
                ),
            ),
        )
        description = "When Cloak and Dagger enter, choose target opponent and up to one target " +
            "creature they control. They reveal their hand. You may exile a nonland card from " +
            "their hand or the chosen creature until Cloak and Dagger leave the battlefield."
    }

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.ReturnLinkedExileToZoneExiledFrom()
        description = "When Cloak and Dagger leave the battlefield, return the exiled card to " +
            "the zone it was exiled from."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "211"
        artist = "Rimas Valeikis"
        imageUri = "https://cards.scryfall.io/normal/front/f/a/fa01d35f-1064-4a7c-9475-4504566df850.jpg?1783902902"
    }
}
