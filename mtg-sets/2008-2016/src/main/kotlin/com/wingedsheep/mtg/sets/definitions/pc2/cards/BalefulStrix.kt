package com.wingedsheep.mtg.sets.definitions.pc2.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Baleful Strix
 * {U}{B}
 * Artifact Creature — Bird
 * 1/1
 *
 * Flying, deathtouch
 * When this creature enters, draw a card.
 */
val BalefulStrix = card("Baleful Strix") {
    manaCost = "{U}{B}"
    colorIdentity = "BU"
    typeLine = "Artifact Creature — Bird"
    oracleText = "Flying, deathtouch\nWhen this creature enters, draw a card."
    power = 1
    toughness = 1

    keywords(Keyword.FLYING, Keyword.DEATHTOUCH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "82"
        artist = "Nils Hamm"
        flavorText = "Its beak rends flesh and bone, exposing the tender marrow of dream."
        imageUri = "https://cards.scryfall.io/normal/front/6/2/62090c97-7e3e-4854-bc44-c4a900133ec5.jpg?1783940604"
    }
}
