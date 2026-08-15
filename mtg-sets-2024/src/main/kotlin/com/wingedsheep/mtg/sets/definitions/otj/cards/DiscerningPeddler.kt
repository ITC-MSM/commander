package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.IfYouDoEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect

/**
 * Discerning Peddler
 * {1}{R}
 * Creature — Human Rogue
 * 2/2
 *
 * When this creature enters, you may discard a card. If you do, draw a card.
 */
val DiscerningPeddler = card("Discerning Peddler") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Rogue"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, you may discard a card. If you do, draw a card."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = MayEffect(
            IfYouDoEffect(
                action = Effects.Discard(1),
                ifYouDo = Effects.DrawCards(1),
            ),
        )
        description = "When this creature enters, you may discard a card. If you do, draw a card."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "121"
        artist = "Josu Hernaiz"
        flavorText = "\"Can I interest you in some herbs from Naktamun? Fresh from the Omenpath! Or a " +
            "set of Fioran cookware? Finest in the Multiverse!\""
        imageUri = "https://cards.scryfall.io/normal/front/7/3/73b359bf-afd8-439d-a4d2-985db1ad368c.jpg?1712355741"
    }
}
