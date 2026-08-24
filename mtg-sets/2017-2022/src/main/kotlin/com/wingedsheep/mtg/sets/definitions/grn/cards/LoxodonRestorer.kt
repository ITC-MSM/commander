package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Loxodon Restorer
 * {4}{W}{W}
 * Creature — Elephant Cleric
 * 3/4
 * Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)
 * When this creature enters, you gain 4 life.
 */
val LoxodonRestorer = card("Loxodon Restorer") {
    manaCost = "{4}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Elephant Cleric"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "When this creature enters, you gain 4 life."
    power = 3
    toughness = 4

    keywords(Keyword.CONVOKE)
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(4)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "20"
        artist = "Svetlin Velinov"
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1ead2166-a7ca-49cb-bf93-2f59c37b4cb9.jpg?1783934196"
    }
}
