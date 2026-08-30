package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Youthful Scholar
 * {3}{U}
 * Creature — Human Wizard
 * 2 / 2
 *
 * When this creature dies, draw two cards.
 *
 * [Triggers.Dies] is the whole card: the battlefield → graveyard zone change with the default
 * battlefield `activeZones`, so the trigger is indexed while the creature is still on the
 * battlefield. The draw goes to the controller, which is the default and therefore unwritten.
 */
val YouthfulScholar = card("Youthful Scholar") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 2
    oracleText = "When this creature dies, draw two cards."

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.DrawCards(2)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "84"
        artist = "Cynthia Sheppard"
        flavorText = "\"Too dumb, and you end up a sibsig. Too smart, and you end up a meal. Mediocrity is the key to a long life.\"\n—Mogai, Silumgar noble"
        imageUri = "https://cards.scryfall.io/normal/front/4/3/43ae8147-bf25-44f9-b75f-837b81ebe0de.jpg?1783938601"
    }
}
