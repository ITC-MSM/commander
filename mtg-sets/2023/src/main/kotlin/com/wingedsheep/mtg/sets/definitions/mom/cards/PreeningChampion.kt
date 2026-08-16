package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Preening Champion
 * {2}{U}
 * Creature — Bird Knight
 * 2/2
 * Flying
 * When this creature enters, create a 1/1 blue and red Elemental creature token.
 */
val PreeningChampion = card("Preening Champion") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird Knight"
    oracleText = "Flying\n" +
        "When this creature enters, create a 1/1 blue and red Elemental creature token."
    power = 2
    toughness = 2

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLUE, Color.RED),
            creatureTypes = setOf("Elemental"),
            imageUri = "https://cards.scryfall.io/normal/front/2/8/28a7a9b0-d823-4b34-829f-ade81fc141e0.jpg?1783916668"
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "73"
        artist = "Alix Branwyn"
        flavorText = "On Kylem, the omens of impending invasion went largely unnoticed, drowned " +
            "out by the everyday fanfare of Valor's Reach."
        imageUri = "https://cards.scryfall.io/normal/front/4/4/44178ece-af31-4a94-88bc-c9ce43bb4573.jpg?1783917028"
    }
}
