package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Squee's Toy
 * {1}
 * Artifact
 * {T}: Prevent the next 1 damage that would be dealt to target creature this turn.
 */
val SqueesToy = card("Squee's Toy") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{T}: Prevent the next 1 damage that would be dealt to target creature this turn."

    activatedAbility {
        cost = Costs.Tap
        val creature = target("target", Targets.Creature)
        effect = Effects.PreventNextDamage(1, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "309"
        artist = "Heather Hudson"
        flavorText = "As the horrors closed in on Gerrard, Squee trembled and clutched his toy for comfort. He didn't know where it came from or why it was so warm, but he was glad he'd kept it near."
        imageUri = "https://cards.scryfall.io/normal/front/2/b/2b524ae7-cb24-41af-b41b-3cb3ee8cf3b0.jpg"
    }
}
