package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Erg Raiders
 * {1}{B}
 * Creature — Human Warrior
 * 2/3
 *
 * Modern oracle (errata from "...didn't come into play under your control this turn" to the
 * SBA-friendly form): "At the beginning of your end step, if this creature didn't attack
 * this turn, it deals 2 damage to you unless it came under your control this turn."
 *
 * Composes from existing primitives plus the new
 * [com.wingedsheep.sdk.scripting.predicates.StatePredicate.AttackedThisTurn] history
 * predicate (per-creature, derived from the controller's
 * [com.wingedsheep.engine.state.components.combat.PlayerAttackersThisTurnComponent]).
 *
 * Intervening-if = NOT attacked-this-turn AND NOT entered-this-turn. The latter is the
 * standard summoning-sickness proxy for "came under your control this turn" — it covers
 * every case except the rare control-change-without-ETB, which is not in scope here.
 */
val ErgRaiders = card("Erg Raiders") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Warrior"
    power = 2
    toughness = 3
    oracleText = "At the beginning of your end step, if this creature didn't attack this " +
        "turn, it deals 2 damage to you unless it came under your control this turn."

    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.All(
            Conditions.Not(Conditions.SourceAttackedThisTurn),
            Conditions.Not(Conditions.SourceEnteredThisTurn),
        )
        effect = Effects.DealDamage(2, EffectTarget.Controller, damageSource = EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "25"
        artist = "Dameon Willich"
        imageUri = "https://cards.scryfall.io/normal/front/3/5/35c73a97-531d-4dd5-8236-39b89c183c38.jpg?1562904951"
    }
}
