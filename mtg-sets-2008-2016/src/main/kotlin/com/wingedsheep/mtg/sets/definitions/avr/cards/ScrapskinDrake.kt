package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CanOnlyBlockCreaturesWith
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Scrapskin Drake
 * {2}{U}
 * Creature — Zombie Drake
 * 2/3
 * Flying
 * This creature can block only creatures with flying.
 */
val ScrapskinDrake = card("Scrapskin Drake") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Zombie Drake"
    oracleText =
        "Flying (This creature can't be blocked except by creatures with flying or reach.)\n" +
            "This creature can block only creatures with flying."
    power = 2
    toughness = 3

    keywords(Keyword.FLYING)

    staticAbility {
        ability = CanOnlyBlockCreaturesWith(
            blockerFilter = GameObjectFilter.Creature.withKeyword(Keyword.FLYING),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "73"
        artist = "Kev Walker"
        flavorText =
            "\"The cathars killed my skaabs down below. Let's see how high their swords can reach.\"\n—Ludevic, necro-alchemist"
        imageUri =
            "https://cards.scryfall.io/normal/front/c/9/c9f03bae-1d23-43ea-9079-4b09d61bbadd.jpg?1783940711"
    }
}
