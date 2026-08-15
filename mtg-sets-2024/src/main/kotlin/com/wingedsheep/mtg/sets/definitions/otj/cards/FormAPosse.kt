package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Form a Posse
 * {X}{R}{W}
 * Sorcery
 *
 * Create X 1/1 red Mercenary creature tokens with "{T}: Target creature you control gets +1/+0
 * until end of turn. Activate only as a sorcery."
 */
val FormAPosse = card("Form a Posse") {
    manaCost = "{X}{R}{W}"
    colorIdentity = "WR"
    typeLine = "Sorcery"
    oracleText = "Create X 1/1 red Mercenary creature tokens with \"{T}: Target creature you " +
        "control gets +1/+0 until end of turn. Activate only as a sorcery.\""

    spell {
        effect = CreateTokenEffect(
            count = DynamicAmount.XValue,
            power = 1,
            toughness = 1,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Mercenary"),
            activatedAbilities = listOf(
                ActivatedAbility(
                    cost = AbilityCost.Tap,
                    effect = Effects.ModifyStats(1, 0, EffectTarget.ContextTarget(0)),
                    targetRequirements = listOf(Targets.CreatureYouControl),
                    timing = TimingRule.SorcerySpeed
                )
            ),
            imageUri = "https://cards.scryfall.io/normal/front/5/f/5f04607f-eed2-462e-897f-82e41e5f7049.jpg?1712316319"
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "204"
        artist = "J.Lonnee"
        flavorText = "The terms were simple: half in advance, half when the entire town belonged to their employer."
        imageUri = "https://cards.scryfall.io/normal/front/3/9/39ee1387-c24e-4a66-8ad6-9afa9c0abcbb.jpg?1712356094"
    }
}
