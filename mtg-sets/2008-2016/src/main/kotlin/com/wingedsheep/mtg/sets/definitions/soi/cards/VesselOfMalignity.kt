package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Vessel of Malignity (Shadows over Innistrad #144)
 * {1}{B}
 * Enchantment
 *
 * {1}{B}, Sacrifice this enchantment: Target opponent exiles two cards from their hand. Activate only as a sorcery.
 */
val VesselOfMalignity = card("Vessel of Malignity") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "{1}{B}, Sacrifice this enchantment: Target opponent exiles two cards from their hand. Activate only as a sorcery."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{B}"), Costs.SacrificeSelf)
        val t = target("target", Targets.Opponent)
        effect = Patterns.Hand.exileFromHand(2, t)
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "144"
        artist = "Kieran Yanner"
        flavorText = "From within its prison, the book endlessly whispers words of cruelty and spite."
        imageUri = "https://cards.scryfall.io/normal/front/8/1/81b44857-1edb-4de4-b646-917101faf881.jpg?1783937759"
    }
}
