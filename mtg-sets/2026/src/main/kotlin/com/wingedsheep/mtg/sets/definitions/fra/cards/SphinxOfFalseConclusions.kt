package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

val SphinxOfFalseConclusions = card("Sphinx of False Conclusions") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Sphinx Illusion"
    power = 4
    toughness = 2
    oracleText = "Flash\n" +
        "Flying\n" +
        "Whenever this creature attacks, draw a card, then discard a card.\n" +
        "When this creature dies, if it isn't a token, create a token that's a copy of it."

    keywords(Keyword.FLASH, Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Patterns.Hand.loot()
    }

    triggeredAbility {
        trigger = Triggers.Dies
        interveningIf = Conditions.SourceMatches(GameObjectFilter.Any.nontoken())
        effect = Effects.CreateTokenCopyOfSelf()
        description = "When this creature dies, if it isn't a token, create a token that's a copy of it."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "40"
        artist = "Wylie Beckert"
        imageUri = "https://cards.scryfall.io/normal/front/0/8/08ffbd51-2bd3-4262-8809-09576ce2b6f5.jpg?1789385594"
        inBooster = false
    }
}
