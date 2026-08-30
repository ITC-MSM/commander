package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Liturgy of Blood
 * {3}{B}{B}
 * Sorcery
 * Destroy target creature. Add {B}{B}{B}.
 */
val LiturgyOfBlood = card("Liturgy of Blood") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Destroy target creature. Add {B}{B}{B}."

    spell {
        val victim = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.Composite(
            Effects.Destroy(victim),
            Effects.AddMana(Color.BLACK, 3)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "104"
        artist = "Zack Stella"
        flavorText = "\"You harbor such vast potential. It would be such a shame to let you die of old age.\"\n" +
            "—Zul Ashur, lich lord"
        imageUri = "https://cards.scryfall.io/normal/front/3/5/3532105d-c550-4c20-8465-a6a19169efbd.jpg"
    }
}
