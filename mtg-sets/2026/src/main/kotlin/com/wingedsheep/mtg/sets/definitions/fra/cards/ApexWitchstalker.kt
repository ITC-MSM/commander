package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

val ApexWitchstalker = card("Apex Witchstalker") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Wolf"
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)\nWhen this creature enters or dies, you gain 2 life.\nBasic landcycling {2} ({2}, Discard this card: Search your library for a basic land card, reveal it, put it into your hand, then shuffle.)"
    power = 6
    toughness = 4

    keywords(Keyword.MENACE)
    keywordAbility(KeywordAbility.basicLandcycling(ManaCost.parse("{2}")))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(2)
    }
    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.GainLife(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "48"
        artist = "Jason Mowry"
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cd56f047-6bdc-4e83-8a7c-923ebad26302.jpg?1789556710"
        inBooster = false
    }
}
