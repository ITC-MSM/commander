package com.wingedsheep.mtg.sets.definitions.mmq.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CanOnlyBlockCreaturesWith
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Rishadan Airship
 * {2}{U}
 * Creature — Human Pirate
 * 3/1
 *
 * Flying
 * This creature can block only creatures with flying.
 */
val RishadanAirship = card("Rishadan Airship") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Pirate"
    oracleText = "Flying\nThis creature can block only creatures with flying."
    power = 3
    toughness = 1

    keywords(Keyword.FLYING)

    staticAbility {
        ability = CanOnlyBlockCreaturesWith(GameObjectFilter.Creature.withKeyword(Keyword.FLYING))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "91"
        artist = "Kev Walker"
        flavorText = "The view is truly spectacular—as long as you don't look at Rishada itself."
        imageUri = "https://cards.scryfall.io/normal/front/5/d/5d8e596b-f5ef-405a-8910-c5d0b5c8c0fc.jpg?1783945964"
    }
}
