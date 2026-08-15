package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility


/**
 * Magitek Armor
 * {3}{W}
 * Artifact — Vehicle
 * 4/4
 * When this Vehicle enters, create a 1/1 colorless Hero creature token.
 * Crew 1 (Tap any number of creatures you control with total power 1 or more: This Vehicle becomes an artifact creature until end of turn.)
 */
val MagitekArmor = card("Magitek Armor") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Artifact — Vehicle"
    oracleText = "When this Vehicle enters, create a 1/1 colorless Hero creature token.\nCrew 1 (Tap any number of creatures you control with total power 1 or more: This Vehicle becomes an artifact creature until end of turn.)"
    power = 4
    toughness = 4
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            creatureTypes = setOf("Hero"),
            imageUri = "https://cards.scryfall.io/normal/front/d/0/d0657ce1-bf75-4007-ac1b-0623eb263357.jpg?1748704030",
        )
    }
    keywordAbility(KeywordAbility.crew(1))
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "24"
        artist = "Nathaniel Himawan"
        imageUri = "https://cards.scryfall.io/normal/front/5/9/59c4a1a2-623c-43b2-8005-ecb5c6436c10.jpg"
    }
}
