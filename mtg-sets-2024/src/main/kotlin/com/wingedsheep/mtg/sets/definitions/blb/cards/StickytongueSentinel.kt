package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Stickytongue Sentinel {2}{G}
 * Creature — Frog Warrior
 * 3/3
 *
 * Reach
 * When this creature enters, return up to one other target permanent
 * you control to its owner's hand.
 */
val StickytongueSentinel = card("Stickytongue Sentinel") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Frog Warrior"
    power = 3
    toughness = 3
    oracleText = "Reach\nWhen this creature enters, return up to one other target permanent you control to its owner's hand."

    keywords(Keyword.REACH)

    // When this creature enters, return up to one other target permanent you control to hand
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val permanent = target(
            "other permanent you control",
            TargetPermanent(optional = true, filter = TargetFilter.PermanentYouControl.other())
        )
        effect = Effects.ReturnToHand(permanent)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "193"
        artist = "Lorenzo Mastroianni"
        flavorText = "Victory doesn't have to be close for him to taste it."
        imageUri = "https://cards.scryfall.io/normal/front/b/5/b5fa9651-b217-4f93-9c46-9bdb11feedcb.jpg?1721426926"
    }
}
