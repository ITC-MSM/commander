package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Barrier of Bones
 * {B}
 * Creature — Skeleton Wall
 * 0/3
 * Defender
 * When this creature enters, surveil 1. (Look at the top card of your library. You may put that card into your graveyard.)
 */
val BarrierOfBones = card("Barrier of Bones") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Skeleton Wall"
    oracleText = "Defender\n" +
        "When this creature enters, surveil 1. (Look at the top card of your library. You may put that card into your graveyard.)"
    power = 0
    toughness = 3

    keywords(Keyword.DEFENDER)
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.surveil(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "61"
        artist = "Vincent Proce"
        flavorText = "The Dimir rarely make statements, but when they do, the message is clear."
        imageUri = "https://cards.scryfall.io/normal/front/2/8/28129aaf-aaff-47f4-8dd2-8c576c55052c.jpg?1783934180"
    }
}
