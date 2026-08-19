package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Agonizing Syphon
 * {3}{B}
 * Sorcery
 *
 * Agonizing Syphon deals 3 damage to any target and you gain 3 life.
 */
val AgonizingSyphon = card("Agonizing Syphon") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Agonizing Syphon deals 3 damage to any target and you gain 3 life."

    spell {
        target = Targets.Any
        effect = Effects.DealDamage(3, EffectTarget.ContextTarget(0))
            .then(Effects.GainLife(3))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "83"
        artist = "Seb McKinnon"
        flavorText = "\"Your death will take a mere moment, but it will feel like an eternity.\"\n—Vilis, Broker of Blood"
        imageUri = "https://cards.scryfall.io/normal/front/0/d/0d8efd95-1c2f-4dd1-b70b-3cfb10ff3a28.jpg?1783933001"
    }
}
