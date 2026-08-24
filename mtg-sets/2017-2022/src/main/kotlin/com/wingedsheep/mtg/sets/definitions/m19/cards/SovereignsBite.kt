package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetPlayer

/**
 * Sovereign's Bite
 * {1}{B}
 * Sorcery
 * Target player loses 3 life and you gain 3 life.
 */
val SovereignsBite = card("Sovereign's Bite") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Target player loses 3 life and you gain 3 life."

    spell {
        val t = target("target", TargetPlayer())
        effect = Effects.Composite(
            Effects.LoseLife(3, t),
            Effects.GainLife(3)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "120"
        artist = "Volkan Baǵa"
        flavorText = "\"You have given all to your kingdom, dear knight. Serenity shall be your prize.\"\n" +
            "—Queen Lian"
        imageUri = "https://cards.scryfall.io/normal/front/5/3/5326d251-bb91-4653-b1fa-44f14c4e0b88.jpg"
    }
}
