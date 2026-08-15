package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Chrome Host Seedshark
 * {2}{U}
 * Creature — Phyrexian Shark
 * 2/4
 *
 * Flying
 * Whenever you cast a noncreature spell, incubate X, where X is that spell's mana value.
 * (Create an Incubator token with X +1/+1 counters on it and "{2}: Transform this token."
 * It transforms into a 0/0 Phyrexian artifact creature.)
 */
val ChromeHostSeedshark = card("Chrome Host Seedshark") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Phyrexian Shark"
    oracleText = "Flying\n" +
        "Whenever you cast a noncreature spell, incubate X, where X is that spell's mana value. " +
        "(Create an Incubator token with X +1/+1 counters on it and \"{2}: Transform this token.\" " +
        "It transforms into a 0/0 Phyrexian artifact creature.)"
    power = 2
    toughness = 4

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = Effects.Incubate(DynamicAmounts.triggeringManaValue())
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "51"
        artist = "Donato Giancola"
        imageUri = "https://cards.scryfall.io/normal/front/f/e/febaaeae-5c0d-45fa-8169-b27b4996f18e.jpg?1682203038"
        ruling("2023-04-14", "If the noncreature spell has {X} in its mana cost, use the value chosen for X when calculating that spell's mana value.")
    }
}
