package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Wardens of the Cycle
 * {1}{B}{G}{G}
 * Creature — Elf Warlock
 * 3/4
 * Morbid — At the beginning of your end step, if a creature died this turn, choose one —
 * • You gain 2 life.
 * • You draw a card and you lose 1 life.
 *
 * "Morbid" is an ability word (no rules meaning); the intervening-if "if a creature died this
 * turn" is modeled as [Conditions.CreatureDiedThisTurn] (CR 603.4 — checked on trigger and at
 * resolution).
 */
val WardensOfTheCycle = card("Wardens of the Cycle") {
    manaCost = "{1}{B}{G}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Elf Warlock"
    power = 3
    toughness = 4
    oracleText = "Morbid — At the beginning of your end step, if a creature died this turn, choose one —\n• You gain 2 life.\n• You draw a card and you lose 1 life."

    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.CreatureDiedThisTurn
        effect = ModalEffect.chooseOne(
            Mode(
                effect = Effects.GainLife(2),
                description = "You gain 2 life"
            ),
            Mode(
                effect = Effects.DrawCards(1).then(Effects.LoseLife(1, EffectTarget.Controller)),
                description = "You draw a card and you lose 1 life"
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "125"
        artist = "Caroline Gariba"
        flavorText = "\"The wheel turns, and what meets its end begins again.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/3/83ea9b2c-5723-4eff-88ac-6669975939e3.jpg?1782689157"
    }
}
