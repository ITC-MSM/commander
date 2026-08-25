package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Familiar's Ruse
 * {U}{U}
 * Instant
 * As an additional cost to cast this spell, return a creature you control to its owner's hand.
 * Counter target spell.
 *
 * The bounce is an *additional cost*, paid on casting — so it happens even if the counter is later
 * fizzled, and a player with no creature can't cast the spell at all.
 */
val FamiliarsRuse = card("Familiar's Ruse") {
    manaCost = "{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "As an additional cost to cast this spell, return a creature you control to its " +
        "owner's hand.\nCounter target spell."

    // `CostAtom.ReturnToHand`'s domain is already `controlledMatching(state, payerId, …)`, so a
    // `youControl()` here would restate the atom's own scope rather than narrow it.
    additionalCost(Costs.additional.ReturnToHand(GameObjectFilter.Creature))

    spell {
        target = Targets.Spell
        effect = Effects.CounterSpell()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "64"
        artist = "Eric Fortune"
        flavorText = "Because of their capricious nature, faeries can serve as living lenses for disruptive magic."
        imageUri = "https://cards.scryfall.io/normal/front/5/5/55b9be91-f3a1-49ce-8a3e-2ecd30e2e692.jpg?1783942904"
    }
}
