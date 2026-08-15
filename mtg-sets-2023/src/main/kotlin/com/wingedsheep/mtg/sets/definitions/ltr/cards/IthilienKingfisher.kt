package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ithilien Kingfisher
 * {2}{U}
 * Creature — Bird
 * 2/1
 * Flying
 * When this creature dies, draw a card.
 */
val IthilienKingfisher = card("Ithilien Kingfisher") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird"
    power = 2
    toughness = 1
    oracleText = "Flying\nWhen this creature dies, draw a card."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "58"
        artist = "Alexander Ostrowski"
        flavorText = "After their reunion, the Hobbits spent many days in Ithilien. The stream that flowed from the falls of Henneth Annûn could be heard in the night as it rushed down through its rocky gate."
        imageUri = "https://cards.scryfall.io/normal/front/6/8/68c29762-6859-4564-9e1d-a87fa63b951a.jpg?1686968173"
    }
}
