package com.wingedsheep.mtg.sets.definitions.sth.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect

/**
 * Wall of Blossoms
 * {1}{G}
 * Creature — Plant Wall
 * 0/4
 *
 * Defender
 * When this creature enters, draw a card.
 */
val WallOfBlossoms = card("Wall of Blossoms") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Plant Wall"
    oracleText = "Defender\nWhen this creature enters, draw a card."
    power = 0
    toughness = 4

    keywords(Keyword.DEFENDER)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = DrawCardsEffect(1)
        description = "When this creature enters, draw a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "125"
        artist = "Heather Hudson"
        flavorText = "Each flower identical, every leaf and petal disturbingly exact."
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7eb4a1a3-efcf-4c9a-ad1f-0a3f8f2b456f.jpg?1783946546"
    }
}
