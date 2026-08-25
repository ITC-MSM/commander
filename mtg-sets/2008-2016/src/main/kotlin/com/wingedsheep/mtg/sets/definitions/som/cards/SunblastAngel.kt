package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Sunblast Angel
 * {4}{W}{W}
 * Creature — Angel
 * 4/5
 *
 * Flying
 * When this creature enters, destroy all tapped creatures.
 */
val SunblastAngel = card("Sunblast Angel") {
    manaCost = "{4}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    power = 4
    toughness = 5
    oracleText = "Flying\n" +
        "When this creature enters, destroy all tapped creatures."
    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DestroyAll(GameObjectFilter.Creature.tapped())
        description = "When this creature enters, destroy all tapped creatures."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "22"
        artist = "Jason Chan"
        flavorText = "There may exist powers even greater than Phyrexia."
        imageUri = "https://cards.scryfall.io/normal/front/3/2/32217d3b-8a44-40e3-a4fd-c849fdffc1e4.jpg?1783941741"
    }
}
