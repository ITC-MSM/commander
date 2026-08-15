package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Stockman, Mad Fly-entist
 * {4}{U}
 * Legendary Creature — Insect Mutant Scientist
 * 3/4
 *
 * Flying
 * When Stockman enters, draw a card, then discard a card.
 * Islandcycling {2}
 */
val StockmanMadFlyEntist = card("Stockman, Mad Fly-entist") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Insect Mutant Scientist"
    oracleText = "Flying\nWhen Stockman enters, draw a card, then discard a card.\nIslandcycling {2} ({2}, Discard this card: Search your library for an Island card, reveal it, put it into your hand, then shuffle.)"
    power = 3
    toughness = 4

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Hand.loot(1)
    }

    keywordAbility(KeywordAbility.typecycling("Island", "{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "54"
        artist = "Xavier Ribeiro"
        imageUri = "https://cards.scryfall.io/normal/front/0/1/01ca65e2-898e-4150-a6bf-e61b183fd98a.jpg?1771502603"
    }
}
