package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Disappearing Act
 * {1}{U}{U}
 * Instant
 * As an additional cost to cast this spell, return a permanent you control to its owner's hand.
 * Counter target spell.
 *
 * The Familiar's Ruse shape with the bounce widened from a creature to any permanent. The return is
 * an *additional cost* (CR 601.2f), paid as the spell is cast — so a player with no permanent can't
 * cast it at all, and the bounce still happens if the counter is later fizzled.
 * `CostAtom.ReturnToHand` already scopes its filter to permanents you control, so the filter here
 * is the bare [GameObjectFilter.Permanent].
 */
val DisappearingAct = card("Disappearing Act") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "As an additional cost to cast this spell, return a permanent you control to its owner's hand.\n" +
        "Counter target spell."

    additionalCost(Costs.additional.ReturnToHand(GameObjectFilter.Permanent))

    spell {
        target("target", Targets.Spell)
        effect = Effects.CounterSpell()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "43"
        artist = "Anthony Palumbo"
        flavorText = "\"Baral took my family from me back then. I'd do anything to face him now.\"\n—Chandra Nalaar"
        imageUri = "https://cards.scryfall.io/normal/front/9/a/9a4a6d56-9bed-444c-aae8-383c315779a0.jpg?1783937222"
    }
}
