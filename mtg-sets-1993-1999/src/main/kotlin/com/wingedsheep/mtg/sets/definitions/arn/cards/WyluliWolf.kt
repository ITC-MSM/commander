package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wyluli Wolf
 * {1}{G}
 * Creature — Wolf
 * 1/1
 * {T}: Target creature gets +1/+1 until end of turn.
 */
val WyluliWolf = card("Wyluli Wolf") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wolf"
    power = 1
    toughness = 1
    oracleText = "{T}: Target creature gets +1/+1 until end of turn."

    activatedAbility {
        cost = Costs.Tap
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(1, 1, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "55"
        artist = "Susan Van Camp"
        flavorText = "\"When one wolf calls, others follow. Who wants to fight creatures that eat scorpions?\" —Maimun al-Wyluli, Diary"
        imageUri = "https://cards.scryfall.io/normal/front/1/5/15ccebe1-ef08-4805-a65f-a1c57abed9f2.jpg?1595373876"
    }
}
