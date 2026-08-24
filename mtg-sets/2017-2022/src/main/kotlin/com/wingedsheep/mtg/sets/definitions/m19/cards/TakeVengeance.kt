package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Take Vengeance
 * {1}{W}
 * Sorcery
 * Destroy target tapped creature.
 */
val TakeVengeance = card("Take Vengeance") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Destroy target tapped creature."

    spell {
        val creature = target("target", Targets.TappedCreature)
        effect = Effects.Destroy(creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "40"
        artist = "Randy Vargas"
        flavorText = "\"Your death will be a balm, your passing a welcome revision, and all will sigh with peace to know of your demise.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/6/66fbde22-d98d-4f12-b4d8-1bad2a9878b2.jpg"
    }
}
