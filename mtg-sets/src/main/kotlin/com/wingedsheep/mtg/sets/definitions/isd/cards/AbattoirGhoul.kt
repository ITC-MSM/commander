package com.wingedsheep.mtg.sets.definitions.isd.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Abattoir Ghoul
 * {3}{B}
 * Creature — Zombie
 * 3/2
 * First strike
 * Whenever a creature dealt damage by this creature this turn dies, you gain life equal to that
 * creature's toughness.
 *
 * Canonical ISD printing. Uses [Triggers.CreatureDealtDamageByThisDies] (same shape as Predator Ooze)
 * with [DynamicAmounts.triggeringToughness] for last-known toughness.
 */
val AbattoirGhoul = card("Abattoir Ghoul") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie"
    oracleText = "First strike\nWhenever a creature dealt damage by this creature this turn dies, " +
        "you gain life equal to that creature's toughness."
    power = 3
    toughness = 2

    keywords(Keyword.FIRST_STRIKE)

    triggeredAbility {
        trigger = Triggers.CreatureDealtDamageByThisDies
        effect = Effects.GainLife(DynamicAmounts.triggeringToughness())
        description =
            "Whenever a creature dealt damage by this creature this turn dies, you gain life " +
                "equal to that creature's toughness."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "85"
        artist = "Volkan Baǵa"
        flavorText = "Death took his humanity but not his skill with the knife."
        imageUri =
            "https://cards.scryfall.io/normal/front/5/9/59cf0906-04fa-4b30-a7a6-3d117931154f.jpg?1783940963"
        ruling(
            "2011-09-22",
            "You'll gain life equal to the creature's last known toughness before it died. " +
                "For example, if Abattoir Ghoul deals 3 first-strike damage to a 7/7 creature and " +
                "then you give the creature -5/-5 before the regular combat damage step, you'll " +
                "gain 2 life.",
        )
    }
}
