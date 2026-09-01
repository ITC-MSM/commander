package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Story Seeker
 * {1}{W}
 * Creature — Dwarf Cleric
 * 2/2
 * Lifelink
 * */
val StorySeeker = card("Story Seeker") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dwarf Cleric"
    oracleText = "Lifelink"
    power = 2
    toughness = 2

    keywords(Keyword.LIFELINK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "34"
        artist = "Svetlin Velinov"
        flavorText = "Dwarven skalds are the most renowned storytellers in all of Kaldheim. They travel across the realms, seeking out heroic deeds and chronicling them in epic sagas."
        imageUri = "https://cards.scryfall.io/normal/front/e/3/e3dae817-3db1-4edf-86ba-c2c2b238fcf5.jpg"
    }
}
