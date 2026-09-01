package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Galepowder Mage
 * {3}{W}
 * Creature — Kithkin Wizard
 * 3/3
 *
 * Flying
 * Whenever this creature attacks, exile another target creature. Return that card to the
 * battlefield under its owner's control at the beginning of the next end step.
 *
 * The exile-and-return-later blink, built as Kykar builds it: an [Effects.Exile] followed by a
 * [CreateDelayedTriggerEffect] on [Step.END] that moves the exiled card back. The delayed
 * trigger is what makes this a *blink* rather than removal — the card leaves and comes back as
 * a new object, so Auras fall off, counters are gone, and enters-the-battlefield triggers fire
 * again.
 *
 * Two details the text carries and the script honors: "another" excludes Galepowder Mage itself
 * ([TargetFilter.OtherCreature]), and the target is any creature, not just yours — the classic
 * use is exiling your own enters-trigger creature, but pointing it at an opposing blocker to
 * remove it from combat is equally legal. "Under its owner's control" is [Zone.BATTLEFIELD]'s
 * default for a returning card, so a creature you stole and then blinked goes home.
 */
val GalepowderMage = card("Galepowder Mage") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kithkin Wizard"
    power = 3
    toughness = 3
    oracleText = "Flying\n" +
        "Whenever this creature attacks, exile another target creature. Return that card to " +
        "the battlefield under its owner's control at the beginning of the next end step."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Attacks
        val creature = target(
            "another target creature",
            TargetCreature(filter = TargetFilter.OtherCreature)
        )
        effect = Effects.Composite(
            Effects.Exile(creature),
            CreateDelayedTriggerEffect(
                step = Step.END,
                effect = Effects.Move(creature, Zone.BATTLEFIELD)
            )
        )
        description = "exile another target creature. Return that card to the battlefield " +
            "under its owner's control at the beginning of the next end step."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "15"
        artist = "Jeremy Jarvis"
        imageUri = "https://cards.scryfall.io/normal/front/8/1/8152999d-5e65-4ea6-b1e7-94b41808faa0.jpg?1783942915"

        ruling(
            "2007-10-01",
            "Galepowder Mage's ability can target a creature controlled by any player. " +
                "It's not optional."
        )
        ruling(
            "2007-10-01",
            "The exiled card will be returned to the battlefield at the beginning of the end " +
                "step even if Galepowder Mage is no longer on the battlefield at that time."
        )
        ruling(
            "2007-10-01",
            "If Galepowder Mage is the only creature on the battlefield when it attacks, its " +
                "ability has no effect."
        )
    }
}
