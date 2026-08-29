package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Aven Battle Priest
 * {5}{W}
 * Creature — Bird Cleric
 * 3/3
 *
 * Flying (This creature can't be blocked except by creatures with flying or reach.)
 * When this creature enters, you gain 3 life.
 */
val AvenBattlePriest = card("Aven Battle Priest") {
    manaCost = "{5}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bird Cleric"
    oracleText = "Flying (This creature can't be blocked except by creatures with flying or reach.)\n" +
        "When this creature enters, you gain 3 life."
    power = 3
    toughness = 3

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "6"
        artist = "John Severin Brassell"
        flavorText = "When the shadow of the aven falls across the battlefield, hope rises in the hearts of the soldiers."
        imageUri = "https://cards.scryfall.io/normal/front/b/0/b0060e75-a5a4-4d9a-894c-45bb7e2feffc.jpg"
    }
}
