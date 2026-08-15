package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetPlayer

/**
 * Heroes' Reunion
 * {G}{W}
 * Instant
 * Target player gains 7 life.
 */
val HeroesReunion = card("Heroes' Reunion") {
    manaCost = "{G}{W}"
    colorIdentity = "GW"
    typeLine = "Instant"
    oracleText = "Target player gains 7 life."

    spell {
        val t = target("target player", TargetPlayer())
        effect = Effects.GainLife(7, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "250"
        artist = "Terese Nielsen"
        flavorText = "\"You helped save my people from a Phyrexian fate. Did you think I wouldn't return the favor?\"\n—Eladamri, to Gerrard"
        imageUri = "https://cards.scryfall.io/normal/front/1/3/135d6043-5ec1-4ad4-8296-41fe23f11cb9.jpg?1562898871"
    }
}
