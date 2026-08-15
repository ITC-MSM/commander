package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Three Bowls of Porridge
 * {2}
 * Artifact — Food
 *
 * {2}, {T}: Choose one that hasn't been chosen —
 * • This artifact deals 2 damage to target creature.
 * • Tap target creature.
 * • Sacrifice this artifact. You gain 3 life.
 *
 * "Choose one that hasn't been chosen" is [ModalEffect.chooseOneNotYetChosen]: the engine
 * remembers which modes this artifact has already chosen and never offers them again.
 */
val ThreeBowlsOfPorridge = card("Three Bowls of Porridge") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact — Food"
    oracleText = "{2}, {T}: Choose one that hasn't been chosen —\n" +
        "• This artifact deals 2 damage to target creature.\n" +
        "• Tap target creature.\n" +
        "• Sacrifice this artifact. You gain 3 life."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap)
        effect = ModalEffect.chooseOneNotYetChosen(
            // • This artifact deals 2 damage to target creature.
            Mode.withTarget(
                Effects.DealDamage(2, EffectTarget.ContextTarget(0), damageSource = EffectTarget.Self),
                Targets.Creature,
                "This artifact deals 2 damage to target creature"
            ),
            // • Tap target creature.
            Mode.withTarget(
                Effects.Tap(EffectTarget.ContextTarget(0)),
                Targets.Creature,
                "Tap target creature"
            ),
            // • Sacrifice this artifact. You gain 3 life.
            Mode.noTarget(
                Effects.Composite(
                    SacrificeSelfEffect,
                    Effects.GainLife(3)
                ),
                "Sacrifice this artifact. You gain 3 life"
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "253"
        artist = "Edgar Sánchez Hidalgo"
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a508e040-d1e5-46aa-8404-1adc18f0f8bd.jpg"
    }
}
