package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Palace Familiar
 * {1}{U}
 * Creature — Bird
 * 1 / 1
 *
 * Flying
 * When this creature dies, draw a card.
 *
 * [Triggers.Dies] already carries the battlefield → graveyard zone change; the ability keeps the
 * default battlefield `activeZones`, because a dies trigger is indexed from where the creature
 * *was*, not from where the card ends up. The draw's controller is the default, so it is left
 * unwritten.
 */
val PalaceFamiliar = card("Palace Familiar") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird"
    power = 1
    toughness = 1
    oracleText = "Flying\n" +
        "When this creature dies, draw a card."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "69"
        artist = "Kev Walker"
        flavorText = "\"The most profound secrets lie in the darkest places of the world. It can be prudent to make use of another set of eyes.\"\n—Sidisi, Silumgar vizier"
        imageUri = "https://cards.scryfall.io/normal/front/f/c/fc0c17c9-54af-4dd4-8d4a-fd5a7b8c3c77.jpg?1783938605"
    }
}
