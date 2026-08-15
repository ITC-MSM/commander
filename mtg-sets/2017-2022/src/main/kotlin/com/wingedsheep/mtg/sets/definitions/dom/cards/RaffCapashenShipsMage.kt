package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantFlashToSpellType

/**
 * Raff Capashen, Ship's Mage
 * {2}{W}{U}
 * Legendary Creature — Human Wizard
 * 3/3
 * Flash
 * Flying
 * You may cast historic spells as though they had flash.
 * (Artifacts, legendaries, and Sagas are historic.)
 */
val RaffCapashenShipsMage = card("Raff Capashen, Ship's Mage") {
    manaCost = "{2}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Legendary Creature — Human Wizard"
    power = 3
    toughness = 3
    oracleText = "Flash\nFlying\nYou may cast historic spells as though they had flash. (Artifacts, legendaries, and Sagas are historic.)"

    keywords(Keyword.FLASH, Keyword.FLYING)

    staticAbility {
        ability = GrantFlashToSpellType(
            filter = GameObjectFilter.Historic,
            controllerOnly = true
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "202"
        artist = "John Stanko"
        imageUri = "https://cards.scryfall.io/normal/front/6/7/674070e3-6efe-45e4-aff6-e4a49ec2106e.jpg?1562737002"
    }
}
