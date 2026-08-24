package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hagra Sharpshooter
 * {2}{B}
 * Creature — Human Assassin Ally
 * 2/2
 * {4}{B}: Target creature gets -1/-1 until end of turn.
 */
val HagraSharpshooter = card("Hagra Sharpshooter") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Assassin Ally"
    power = 2
    toughness = 2
    oracleText = "{4}{B}: Target creature gets -1/-1 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{4}{B}")
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(-1, -1, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "113"
        artist = "Josu Hernaiz"
        flavorText = "\"It's hard to find their weak points, but I very much enjoy the discovery process.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/7/a706b2e3-bd0e-4111-8b68-976869c7d707.jpg?1783938201"
    }
}
