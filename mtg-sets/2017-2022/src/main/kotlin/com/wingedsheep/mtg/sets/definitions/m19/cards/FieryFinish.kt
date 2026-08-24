package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fiery Finish
 * {4}{R}{R}
 * Sorcery
 * Fiery Finish deals 7 damage to target creature.
 */
val FieryFinish = card("Fiery Finish") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Fiery Finish deals 7 damage to target creature."

    spell {
        val t = target("target creature", Targets.Creature)
        effect = Effects.DealDamage(7, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "140"
        artist = "Joe Slucher"
        flavorText = "Negotiations reached an abrupt conclusion."
        imageUri = "https://cards.scryfall.io/normal/front/3/e/3e38127d-63af-4d26-9ff5-358c8a61f39c.jpg"
    }
}
