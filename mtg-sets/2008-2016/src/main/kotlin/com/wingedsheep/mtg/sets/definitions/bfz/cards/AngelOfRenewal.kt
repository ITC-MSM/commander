package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Angel of Renewal
 * {5}{W}
 * Creature — Angel Ally
 * 4/4
 * Flying
 * When this creature enters, you gain 1 life for each creature you control.
 */
val AngelOfRenewal = card("Angel of Renewal") {
    manaCost = "{5}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel Ally"
    power = 4
    toughness = 4
    oracleText = "Flying\n" +
        "When this creature enters, you gain 1 life for each creature you control."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(DynamicAmounts.creaturesYouControl())
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "18"
        artist = "Todd Lockwood"
        flavorText = "\"No more fear. No more failure. No more death. No more!\""
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0f882ac2-3cbc-4548-8c83-d2a7443991df.jpg?1783938222"
    }
}
