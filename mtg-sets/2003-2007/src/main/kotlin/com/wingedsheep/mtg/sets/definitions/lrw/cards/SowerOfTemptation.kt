package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration

/**
 * Sower of Temptation
 * {2}{U}{U}
 * Creature — Faerie Wizard
 * 2/2
 * Flying
 * When this creature enters, gain control of target creature for as long as this creature
 * remains on the battlefield.
 *
 * The control change is a CR 611.2b "for as long as …" continuous effect, so it rides
 * [Duration.WhileSourceOnBattlefield] keyed to Sower rather than being a static ability on Sower
 * itself: the effect belongs to the resolving enters trigger, and `EndedDurationExpiryCheck`
 * hands the creature back the instant Sower leaves the battlefield.
 *
 * The duration matters more than it looks. Because the grab is a one-shot trigger with a
 * source-keyed duration and *not* a static ability, killing Sower in response to the trigger
 * means the trigger resolves with its duration already ended — the creature is never stolen at
 * all (its own ruling). Bouncing Sower after the fact likewise returns the creature immediately,
 * and a second Sower entering later starts a fresh, independent grab.
 */
val SowerOfTemptation = card("Sower of Temptation") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Faerie Wizard"
    power = 2
    toughness = 2
    oracleText = "Flying\n" +
        "When this creature enters, gain control of target creature for as long as this creature " +
        "remains on the battlefield."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target creature", Targets.Creature)
        effect = Effects.GainControl(
            creature,
            Duration.WhileSourceOnBattlefield("Sower of Temptation")
        )
        description = "gain control of target creature for as long as this creature remains on the battlefield."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "88"
        artist = "Christopher Moeller"
        flavorText = "One glamer leads him far from home. The next washes away his memory that home " +
            "was ever anywhere but at her side."
        imageUri = "https://cards.scryfall.io/normal/front/2/f/2f5320da-7214-4348-84d8-74bf951c9f2f.jpg?1783942897"
    }
}
