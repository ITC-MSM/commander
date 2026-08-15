package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Mongoose Lizard
 * {4}{R}{R}
 * Creature — Mongoose Lizard
 * 5/6
 * Menace (This creature can't be blocked except by two or more creatures.)
 * When this creature enters, it deals 1 damage to any target.
 * Mountaincycling {2} ({2}, Discard this card: Search your library for a Mountain card,
 * reveal it, put it into your hand, then shuffle.)
 *
 * Mountaincycling is Typecycling for the Mountain land type ([KeywordAbility.typecycling]).
 */
val MongooseLizard = card("Mongoose Lizard") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Mongoose Lizard"
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)\n" +
        "When this creature enters, it deals 1 damage to any target.\n" +
        "Mountaincycling {2} ({2}, Discard this card: Search your library for a Mountain card, " +
        "reveal it, put it into your hand, then shuffle.)"
    power = 5
    toughness = 6

    keywords(Keyword.MENACE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(1, t)
    }

    keywordAbility(KeywordAbility.typecycling("Mountain", ManaCost.parse("{2}")))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "148"
        artist = "Joseph Weston"
        imageUri = "https://cards.scryfall.io/normal/front/7/d/7d40b9cb-82ef-44c5-8d3c-c8bb4ce9891c.jpg?1764121019"
    }
}
