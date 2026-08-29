package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Rumbling Sentry
 * {3}{W}{W}
 * Creature — Giant
 * 3/6
 *
 * When this creature enters, scry 1.
 *
 * The same shape as this set's Thaumaturge's Familiar: a self-ETB into [Patterns.Library.scry],
 * whose no-target overload is the bare `ScryEffect` the controller performs.
 */
val RumblingSentry = card("Rumbling Sentry") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Giant"
    power = 3
    toughness = 6
    oracleText = "When this creature enters, scry 1."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.scry(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "35"
        artist = "Yongjae Choi"
        flavorText = "\"To provoke the mountain is to invite the avalanche.\"\n—Perisophia the philosopher"
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4f93f3c9-b317-40c1-87f5-0038c09b646d.jpg"
    }
}
