package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Voidwielder
 * {4}{U}
 * Creature — Human Wizard
 * 1/4
 *
 * When this creature enters, you may return target creature to its owner's hand.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * Batterhorn's shape in blue: `optional = true` lowers to one `MayEffect` consent gate, asked at
 * resolution after the target was locked in on announcement.
 */
val Voidwielder = card("Voidwielder") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    oracleText = "When this creature enters, you may return target creature to its owner's hand."
    power = 1
    toughness = 4

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        val t = target("target creature", Targets.Creature)
        effect = Effects.ReturnToHand(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "56"
        artist = "Chase Stone"
        flavorText = "\"He makes up his own laws, and that's dangerous to all who love peace and prosperity. Kill him on sight.\"\n" +
            "—Mirela, Azorius hussar"
        imageUri = "https://cards.scryfall.io/normal/front/2/3/23723bc7-a68e-4810-bc87-60df916cbb8a.jpg?1783940365"
    }
}
