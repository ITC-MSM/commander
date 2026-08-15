package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Deathbloom Thallid
 * {2}{B}
 * Creature — Fungus
 * 3/2
 * When this creature dies, create a 1/1 green Saproling creature token.
 */
val DeathbloomThallid = card("Deathbloom Thallid") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Fungus"
    power = 3
    toughness = 2
    oracleText = "When this creature dies, create a 1/1 green Saproling creature token."

    triggeredAbility {
        trigger = Triggers.Dies
        effect = CreateTokenEffect(
            count = 1,
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Saproling"),
            imageUri = "https://cards.scryfall.io/normal/front/5/3/5371de1b-db33-4db4-a518-e35c71aa72b7.jpg?1562702067"
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "84"
        artist = "Mike Burns"
        flavorText = "\"Nature is not always gentle or kind, but all life begets life.\" —Marwyn of Llanowar"
        imageUri = "https://cards.scryfall.io/normal/front/2/3/236ead00-d43e-4079-b74d-09be9875fd2d.jpg?1592317493"
    }
}
