package com.wingedsheep.mtg.sets.definitions.hou.cards

import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect

/**
 * Feral Prowler
 * {1}{G}
 * Creature — Cat
 * 1/3
 *
 * When this creature dies, draw a card.
 */
val FeralProwler = card("Feral Prowler") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Cat"
    oracleText = "When this creature dies, draw a card."
    power = 1
    toughness = 3

    triggeredAbility {
        trigger = Triggers.Dies
        effect = DrawCardsEffect(1)
        description = "When this creature dies, draw a card."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "115"
        artist = "Ben Wootten"
        flavorText = "Once favored companions, many cats were left to fend for themselves."
        imageUri = "https://cards.scryfall.io/normal/front/b/a/ba0431ad-185a-4917-b994-e58dd9850f5e.jpg?1783936020"
    }
}
