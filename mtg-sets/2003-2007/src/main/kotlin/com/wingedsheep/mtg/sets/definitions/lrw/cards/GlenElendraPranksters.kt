package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayEffect

/**
 * Glen Elendra Pranksters
 * {3}{U}
 * Creature — Faerie Wizard
 * 1/3
 * Flying
 * Whenever you cast a spell during an opponent's turn, you may return target creature you control
 * to its owner's hand.
 *
 * The blue half of the [DreamspoilerWitches] cycle: the same `triggerRestriction`
 * ([Conditions.IsNotYourTurn]) over a different payoff, here rebuying your own enters-the-
 * battlefield creatures at instant speed.
 *
 * The bounce targets a creature *you* control, and it goes to its **owner's** hand — a creature you
 * gained control of returns to the player who owns it, not to you.
 */
val GlenElendraPranksters = card("Glen Elendra Pranksters") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Faerie Wizard"
    power = 1
    toughness = 3
    oracleText = "Flying\n" +
        "Whenever you cast a spell during an opponent's turn, you may return target creature you " +
        "control to its owner's hand."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YouCastSpell
        triggerRestriction = Conditions.IsNotYourTurn
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = MayEffect(Effects.ReturnToHand(creature))
        description = "Whenever you cast a spell during an opponent's turn, you may return target " +
            "creature you control to its owner's hand."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "67"
        artist = "Omar Rayyan"
        flavorText = "Victims spirited through a faerie ring find themselves stranded miles away."
        imageUri = "https://cards.scryfall.io/normal/front/2/a/2a71800f-3e67-4cfb-a3b1-8c073a91b909.jpg?1783942901"
    }
}
