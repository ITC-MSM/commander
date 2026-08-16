package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tura Kennerüd, Skyknight
 * {2}{W}{U}{U}
 * Legendary Creature — Human Knight
 * 3/3
 * Flying
 * Whenever you cast an instant or sorcery spell, create a 1/1 white Soldier creature token.
 */
val TuraKennerudSkyknight = card("Tura Kennerüd, Skyknight") {
    manaCost = "{2}{W}{U}{U}"
    colorIdentity = "WU"
    typeLine = "Legendary Creature — Human Knight"
    oracleText = "Flying\nWhenever you cast an instant or sorcery spell, create a 1/1 white Soldier creature token."
    power = 3
    toughness = 3

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YouCastInstantOrSorcery
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Soldier")
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "224"
        artist = "Donato Giancola"
        flavorText = "\"The sky is my birthright. I dare you to try and take it from me.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/8/c839d302-bf85-4a30-a49a-dbbd48695b5c.jpg?1783921274"
    }
}
