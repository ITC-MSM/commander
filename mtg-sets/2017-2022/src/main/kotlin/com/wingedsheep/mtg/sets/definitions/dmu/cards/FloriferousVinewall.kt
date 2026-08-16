package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Floriferous Vinewall
 * {1}{G}
 * Creature — Plant Wall
 * 0/2
 * Defender
 * When this creature enters, look at the top six cards of your library. You may reveal a land card from among them and put it into your hand. Put the rest on the bottom of your library in a random order.
 */
val FloriferousVinewall = card("Floriferous Vinewall") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Plant Wall"
    oracleText = "Defender\nWhen this creature enters, look at the top six cards of your library. You may reveal a land card from among them and put it into your hand. Put the rest on the bottom of your library in a random order."
    power = 0
    toughness = 2

    keywords(Keyword.DEFENDER)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.lookAtTopRevealMatchingToHand(
            count = DynamicAmount.Fixed(6),
            filter = GameObjectFilter.Land,
            prompt = "You may reveal a land card from among them and put it into your hand"
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "163"
        artist = "Jakub Kasper"
        imageUri = "https://cards.scryfall.io/normal/front/8/3/8382b7c7-762e-43f6-b285-e0cebe749fab.jpg?1783921303"
    }
}
