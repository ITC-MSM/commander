package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Yavimaya Sapherd
 * {2}{G}
 * Creature — Fungus
 * 2/2
 * When this creature enters, create a 1/1 green Saproling creature token.
 */
val YavimayaSapherd = card("Yavimaya Sapherd") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Fungus"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, create a 1/1 green Saproling creature token."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = CreateTokenEffect(
            count = 1,
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Saproling"),
            imageUri = "https://cards.scryfall.io/normal/front/3/4/34032448-fe31-44c7-845c-37fea0b8e762.jpg?1767955055"
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "189"
        artist = "Christine Choi"
        flavorText = "\"When their community grows cluttered, thallids begin a traditional bobbing dance, then trek out in all directions.\" —Sarpadian Empires, vol. III"
        imageUri = "https://cards.scryfall.io/normal/front/5/e/5ef6b657-7273-4abe-92f4-e1e4cda78f96.jpg?1562736504"
    }
}
