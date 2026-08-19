package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bulwark Giant
 * {5}{W}
 * Creature — Giant Soldier
 * 3/6
 *
 * When this creature enters, you gain 5 life.
 */
val BulwarkGiant = card("Bulwark Giant") {
    manaCost = "{5}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Giant Soldier"
    oracleText = "When this creature enters, you gain 5 life."
    power = 3
    toughness = 6

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(5)
        description = "When this creature enters, you gain 5 life."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "7"
        artist = "Victor Adame Minguez"
        flavorText = "\"Where did she come from? More importantly, are there more like her?\"\n—Gideon Jura"
        imageUri = "https://cards.scryfall.io/normal/front/1/1/11510817-edb3-40d4-bd27-6161fedadd11.jpg?1783933488"
    }
}
