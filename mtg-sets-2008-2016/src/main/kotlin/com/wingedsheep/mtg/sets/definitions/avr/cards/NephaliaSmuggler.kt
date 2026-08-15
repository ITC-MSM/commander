package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Nephalia Smuggler
 * {U}
 * Creature — Human Rogue
 * 1/1
 * {3}{U}, {T}: Exile another target creature you control, then return that card to the
 * battlefield under your control.
 */
val NephaliaSmuggler = card("Nephalia Smuggler") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Rogue"
    oracleText =
        "{3}{U}, {T}: Exile another target creature you control, then return that card to the " +
            "battlefield under your control."
    power = 1
    toughness = 1

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{U}"), Costs.Tap)
        val creature = target("another target creature you control", Targets.OtherCreatureYouControl)
        effect = Effects.Exile(creature)
            .then(
                Effects.Move(
                    creature,
                    Zone.BATTLEFIELD,
                    controllerOverride = EffectTarget.Controller,
                ),
            )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "69"
        artist = "Matt Stewart"
        flavorText = "\"My drivers are trustworthy. I removed their tongues myself.\""
        imageUri =
            "https://cards.scryfall.io/normal/front/1/a/1a531b2f-2a9e-4cc9-aea6-9dce239f5511.jpg?1592708698"
    }
}
