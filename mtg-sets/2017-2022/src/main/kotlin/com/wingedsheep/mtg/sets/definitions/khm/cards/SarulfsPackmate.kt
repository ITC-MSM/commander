package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Sarulf's Packmate
 * {3}{G}
 * Creature — Wolf
 * 3/3
 * When this creature enters, draw a card.
 * Foretell {1}{G} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)
 *
 * A cantrip body. Foretelling it splits the four mana across two turns, which is the whole design.
 */
val SarulfsPackmate = card("Sarulf's Packmate") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wolf"
    oracleText = "When this creature enters, draw a card.\n" +
        "Foretell {1}{G} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)"
    power = 3
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    keywordAbility(KeywordAbility.foretell("{1}{G}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "192"
        artist = "Ilse Gort"
        imageUri = "https://cards.scryfall.io/normal/front/6/0/6061113e-7dd8-4739-b4dd-55bb7f9e39a2.jpg"
    }
}
