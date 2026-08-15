package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect


/**
 * Adventurer's Airship
 * {3}
 * Artifact — Vehicle
 * 3/2
 * Flying
 * Whenever this Vehicle attacks, draw a card, then discard a card.
 * Crew 2 (Tap any number of creatures you control with total power 2 or more: This Vehicle becomes an artifact creature until end of turn.)
 */
val AdventurersAirship = card("Adventurer's Airship") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact — Vehicle"
    oracleText = "Flying\nWhenever this Vehicle attacks, draw a card, then discard a card.\nCrew 2 (Tap any number of creatures you control with total power 2 or more: This Vehicle becomes an artifact creature until end of turn.)"
    power = 3
    toughness = 2
    keywords(Keyword.FLYING)
    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.Composite(
            DrawCardsEffect(1),
            Patterns.Hand.discardCards(1)
        )
    }
    keywordAbility(KeywordAbility.crew(2))
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "252"
        artist = "Racrufi"
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0a1d6dcd-bd41-4f57-a35b-6613811fe4d4.jpg"
    }
}
