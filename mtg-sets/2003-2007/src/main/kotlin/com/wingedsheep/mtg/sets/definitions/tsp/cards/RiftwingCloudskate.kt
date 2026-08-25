package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Riftwing Cloudskate
 * {3}{U}{U}
 * Creature — Illusion
 * 2 / 2
 * Flying
 * When this creature enters, return target permanent to its owner's hand.
 * Suspend 3—{1}{U} (Rather than cast this card from your hand, you may pay {1}{U} and exile it with three time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost. It has haste.)
 *
 * The bounce is unrestricted — [Targets.Permanent], not a nonland filter — so the trigger can
 * take a land, its own controller's permanent, or anything else on the battlefield.
 */
val RiftwingCloudskate = card("Riftwing Cloudskate") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Illusion"
    power = 2
    toughness = 2
    oracleText = "Flying\n" +
        "When this creature enters, return target permanent to its owner's hand.\n" +
        "Suspend 3—{1}{U} (Rather than cast this card from your hand, you may pay {1}{U} and exile it with three time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost. It has haste.)"

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", Targets.Permanent)
        effect = Effects.ReturnToHand(t)
    }

    keywordAbility(KeywordAbility.suspend("{1}{U}", 3))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "73"
        artist = "Carl Critchlow"
        imageUri = "https://cards.scryfall.io/normal/front/7/8/78ad6c86-8cfb-4f24-b8e8-ecbeffc949b8.jpg"
    }
}
