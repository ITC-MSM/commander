package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Kin-Tree Invocation
 * {B}{G}
 * Sorcery
 * Create an X/X black and green Spirit Warrior creature token,
 * where X is the greatest toughness among creatures you control.
 */
val KinTreeInvocation = card("Kin-Tree Invocation") {
    manaCost = "{B}{G}"
    colorIdentity = "BG"
    typeLine = "Sorcery"
    oracleText = "Create an X/X black and green Spirit Warrior creature token, where X is the greatest toughness among creatures you control."

    spell {
        effect = Effects.CreateDynamicToken(
            dynamicPower = DynamicAmounts.battlefield(Player.You, GameObjectFilter.Creature).maxToughness(),
            dynamicToughness = DynamicAmounts.battlefield(Player.You, GameObjectFilter.Creature).maxToughness(),
            colors = setOf(Color.BLACK, Color.GREEN),
            creatureTypes = setOf("Spirit", "Warrior")
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "183"
        artist = "Ryan Alexander Lee"
        flavorText = "The passing years add new rings to the tree's trunk, bolstering the spirits that dwell within."
        imageUri = "https://cards.scryfall.io/normal/front/9/2/926b49a1-f220-41ab-8c67-8354a91a15e8.jpg?1562790454"
    }
}
