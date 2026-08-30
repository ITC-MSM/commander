package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Zephyr Charge
 * {1}{U}
 * Enchantment
 * {1}{U}: Target creature gains flying until end of turn.
 */
val ZephyrCharge = card("Zephyr Charge") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "{1}{U}: Target creature gains flying until end of turn."

    activatedAbility {
        cost = Costs.Mana("{1}{U}")
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.GrantKeyword(Keyword.FLYING, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "82"
        artist = "Steve Prescott"
        flavorText = "\"All armies prefer high ground to low and sunny places to dark.\"\n" +
            "—Sun Tzu, *Art of War*, trans. Giles"
        imageUri = "https://cards.scryfall.io/normal/front/f/9/f9ea2808-0dde-4065-ae7d-905aae98703f.jpg"
    }
}
