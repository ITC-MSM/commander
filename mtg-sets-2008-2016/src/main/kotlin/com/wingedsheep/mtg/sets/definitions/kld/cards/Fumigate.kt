package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Fumigate
 * {3}{W}{W}
 * Sorcery
 * Destroy all creatures. You gain 1 life for each creature destroyed this way.
 *
 * Creatures can be regenerated (no "can't be regenerated" clause), and the life gain
 * counts only creatures actually destroyed — [Effects.DestroyAll.storeDestroyedAs] records
 * the destroyed set, whose count feeds the life gain (mirrors Decree of Pain's draw-per-kill).
 */
val Fumigate = card("Fumigate") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Destroy all creatures. You gain 1 life for each creature destroyed this way."

    spell {
        effect = Effects.DestroyAll(
            filter = GameObjectFilter.Creature,
            storeDestroyedAs = "destroyed"
        ).then(
            Effects.GainLife(DynamicAmount.VariableReference("destroyed_count"))
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "15"
        artist = "Svetlin Velinov"
        flavorText = "\"Ghirapur's gremlin population poses a threat to the infrastructure of the " +
            "fairgrounds. Threats must be eliminated.\"\n—Sram, senior edificer"
        imageUri = "https://cards.scryfall.io/normal/front/f/0/f00f27a7-9e92-4fbf-baa8-f47a5eee48a6.jpg?1782711619"
    }
}
