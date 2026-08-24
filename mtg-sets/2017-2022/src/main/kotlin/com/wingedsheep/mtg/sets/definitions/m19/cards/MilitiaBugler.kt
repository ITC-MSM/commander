package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Militia Bugler
 * {2}{W}
 * Creature — Human Soldier
 * 2/3
 * Vigilance (Attacking doesn't cause this creature to tap.)
 * When this creature enters, look at the top four cards of your library. You may reveal a creature card with power 2 or less from among them and put it into your hand. Put the rest on the bottom of your library in a random order.
 *
 * The whole enters trigger is one composition: [Patterns.Library.lookAtTopRevealMatchingToHand]
 * gathers the top four, offers *up to one* matching card, reveals the kept card into hand, and
 * bottoms the remainder in a random order — the pattern's own defaults supply the destination and
 * the shuffle, so only the count, the filter, and the prompt are card-specific.
 */
val MilitiaBugler = card("Militia Bugler") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 3
    oracleText = "Vigilance (Attacking doesn't cause this creature to tap.)\n" +
        "When this creature enters, look at the top four cards of your library. You may reveal a creature card with power 2 or less from among them and put it into your hand. Put the rest on the bottom of your library in a random order."

    keywords(Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.lookAtTopRevealMatchingToHand(
            count = DynamicAmount.Fixed(4),
            filter = GameObjectFilter.Creature.powerAtMost(2),
            prompt = "You may reveal a creature card with power 2 or less from among them and put it into your hand"
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "29"
        artist = "David Gaillet"
        imageUri = "https://cards.scryfall.io/normal/front/4/3/43c5bf25-937c-4e17-9ed4-b4c4579fa9dc.jpg"
    }
}
