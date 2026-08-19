package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tarfire
 * {R}
 * Kindred Instant — Goblin
 * Tarfire deals 2 damage to any target.
 *
 * A Shock that is also a Goblin card — which is the whole point: Boggart Harbinger finds it,
 * Boggart Birth Rite rebuys it, and Bog-Strider Ash sees it cast.
 */
val Tarfire = card("Tarfire") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Kindred Instant — Goblin"
    oracleText = "Tarfire deals 2 damage to any target."

    spell {
        val recipient = target("any target", Targets.Any)
        effect = Effects.DealDamage(2, recipient)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "194"
        artist = "Omar Rayyan"
        flavorText = "\"After Auntie brushed the soot from her eyes, she discovered something wonderful: the fire had turned the goat into something that smelled delicious.\"\n—A tale of Auntie Grub"
        imageUri = "https://cards.scryfall.io/normal/front/d/1/d13a898e-6a97-4fd9-980e-3bfd8d755386.jpg?1783942869"
    }
}
