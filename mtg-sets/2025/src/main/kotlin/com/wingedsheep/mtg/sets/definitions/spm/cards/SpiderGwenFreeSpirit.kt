package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.effects.IfYouDoEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect

/**
 * Spider-Gwen, Free Spirit
 * {2}{R}
 * Legendary Creature — Spider Human Hero, 2/3
 * Reach
 * Whenever Spider-Gwen becomes tapped, you may discard a card. If you do, draw a card.
 */
val SpiderGwenFreeSpirit = card("Spider-Gwen, Free Spirit") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Spider Human Hero"
    oracleText = "Reach\nWhenever Spider-Gwen becomes tapped, you may discard a card. If you do, draw a card."
    power = 2
    toughness = 3
    keywords(Keyword.REACH)
    triggeredAbility {
        trigger = Triggers.BecomesTapped
        effect = MayEffect(effect = IfYouDoEffect(action = Patterns.Hand.discardCards(1), ifYouDo = DrawCardsEffect(1)))
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "90"
        artist = "Lie Setiawan"
        flavorText = "At the edge of the Spider-Verse, a Gwen Stacy becomes a hero."
        imageUri = "https://cards.scryfall.io/normal/front/3/b/3bc04fa7-6265-4549-91f6-eebdcd67398a.jpg?1757377352"
    }
}
