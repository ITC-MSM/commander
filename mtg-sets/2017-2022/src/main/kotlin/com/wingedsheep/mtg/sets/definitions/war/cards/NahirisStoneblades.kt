package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Nahiri's Stoneblades
 * {1}{R}
 * Instant
 * Up to two target creatures each get +2/+0 until end of turn.
 *
 * "Up to two target creatures **each** get +2/+0" is a plural requirement, so the pump runs
 * once per chosen target ([ForEachTargetEffect] over `ContextTarget(0)`) rather than once
 * against the requirement as a whole — binding the multi-slot handle directly resolves null.
 */
val NahirisStoneblades = card("Nahiri's Stoneblades") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Up to two target creatures each get +2/+0 until end of turn."

    spell {
        target("target", Targets.UpToCreatures(2))
        effect = ForEachTargetEffect(
            listOf(Effects.ModifyStats(2, 0, EffectTarget.ContextTarget(0)))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "139"
        artist = "Micah Epstein"
        flavorText = "The ancient Planeswalkers Sorin and Nahiri battled across Ravnica, their blows cutting as deep as their grudge."
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0a14331e-8da5-4455-ac69-e510684e989c.jpg"
    }
}
