package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Violent Ultimatum
 * {B}{B}{R}{R}{R}{G}{G}
 * Sorcery
 * Destroy three target permanents.
 *
 * The count lives entirely on the target requirement — one [TargetPermanent] with `count = 3` — and
 * the effect is fanned out over it with [ForEachTargetEffect], so each chosen permanent is destroyed
 * in its own iteration with [EffectTarget.ContextTarget] `(0)` bound to it. That keeps
 * indestructible and regeneration per-permanent (they are three separate [Effects.Destroy]
 * applications, not one group destroy) and means the number of targets is owned by the requirement
 * rather than duplicated on the effect.
 */
val ViolentUltimatum = card("Violent Ultimatum") {
    manaCost = "{B}{B}{R}{R}{R}{G}{G}"
    colorIdentity = "BGR"
    typeLine = "Sorcery"
    oracleText = "Destroy three target permanents."

    spell {
        target = TargetPermanent(count = 3)
        effect = ForEachTargetEffect(
            effects = listOf(Effects.Destroy(EffectTarget.ContextTarget(0)))
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "206"
        artist = "Raymond Swanland"
        flavorText = "\"Words are a waste of time. Destruction is a language everyone understands.\"\n—Sarkhan Vol"
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7e6ac9ce-e163-426f-8fbd-5ee1ec177dc1.jpg"
    }
}
