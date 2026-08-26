package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Final Reward
 * {4}{B}
 * Instant
 * Exile target creature.
 */
val FinalReward = card("Final Reward") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Exile target creature."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Exile(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "92"
        artist = "Sidharth Chaturvedi"
        flavorText = "Those who earn a glorious death are given the highest honor. They are carried on funeral barges through the gate to the afterlife."
        imageUri = "https://cards.scryfall.io/normal/front/8/f/8f202f6b-710f-4376-a49c-e5f135b26eaf.jpg?1783936505"
    }
}
