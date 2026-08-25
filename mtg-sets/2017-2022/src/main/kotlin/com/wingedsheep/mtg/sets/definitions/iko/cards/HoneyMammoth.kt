package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Honey Mammoth
 * {4}{G}{G}
 * Creature — Elephant
 * 6/6
 * When this creature enters, you gain 4 life.
 */
val HoneyMammoth = card("Honey Mammoth") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elephant"
    power = 6
    toughness = 6
    oracleText = "When this creature enters, you gain 4 life."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(4)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "158"
        artist = "Lars Grant-West"
        flavorText = "\"And I thought *I* had a big sweet tooth.\"\n—Gannet, Skysail zoologist"
        imageUri = "https://cards.scryfall.io/normal/front/8/4/84b9bee2-b973-4de7-b72d-7f36f8e8153c.jpg"
    }
}
