package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Elite Guardmage
 * {2}{W}{U}
 * Creature — Human Wizard
 * 2/3
 * Flying
 * When this creature enters, you gain 3 life and draw a card.
 */
val EliteGuardmage = card("Elite Guardmage") {
    manaCost = "{2}{W}{U}"
    colorIdentity = "UW"
    typeLine = "Creature — Human Wizard"
    oracleText = "Flying\n" +
        "When this creature enters, you gain 3 life and draw a card."
    power = 2
    toughness = 3

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(3) then Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "195"
        artist = "PINDURSKI"
        flavorText = "\"Be careful. You'll have more than Dovin to contend with if you hope to breach New Prahv.\"\n—Lavinia, to Chandra Nalaar"
        imageUri = "https://cards.scryfall.io/normal/front/d/2/d2e40b14-48a2-4b9e-b767-e914cab04b30.jpg"
    }
}
