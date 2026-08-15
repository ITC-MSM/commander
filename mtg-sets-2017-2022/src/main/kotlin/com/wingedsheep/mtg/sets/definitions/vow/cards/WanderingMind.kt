package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Wandering Mind
 * {1}{U}{R}
 * Creature — Horror
 * 2/1
 *
 * Flying
 * When this creature enters, look at the top six cards of your library. You may reveal a
 * noncreature, nonland card from among them and put it into your hand. Put the rest on the
 * bottom of your library in a random order.
 */
val WanderingMind = card("Wandering Mind") {
    manaCost = "{1}{U}{R}"
    colorIdentity = "UR"
    typeLine = "Creature — Horror"
    power = 2
    toughness = 1
    oracleText = "Flying\nWhen this creature enters, look at the top six cards of your library. " +
        "You may reveal a noncreature, nonland card from among them and put it into your hand. " +
        "Put the rest on the bottom of your library in a random order."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.lookAtTopRevealMatchingToHand(
            count = DynamicAmount.Fixed(6),
            filter = GameObjectFilter.Noncreature and GameObjectFilter.Nonland,
            prompt = "You may reveal a noncreature, nonland card and put it into your hand"
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "251"
        artist = "Simon Dominic"
        imageUri = "https://cards.scryfall.io/normal/front/4/b/4b9a1096-6313-4245-be18-1588c219b842.jpg?1782703017"
    }
}
