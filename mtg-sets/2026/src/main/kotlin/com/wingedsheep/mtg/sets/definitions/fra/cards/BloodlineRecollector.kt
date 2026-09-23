package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Bloodline Recollector // Ancestral Craving — Reality Fracture #49
 * {1}{B} · Creature — Vampire Warlock · 2/2
 *
 * At the beginning of each end step, if three or more creatures died this turn, this creature
 * becomes prepared. (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)
 * //
 * Ancestral Craving — {B}, Instant: Target player draws three cards and loses 3 life.
 *
 * Prepare: this creature does NOT enter prepared (no PREPARED keyword). Its end-step trigger fires
 * on *each* end step (yours and opponents') and is gated as an intervening-if (CR 603.4) on three
 * or more creatures having died this turn — a global count across all players, expressed as
 * `creaturesDiedThisTurn(Player.Each) >= 3` (tokens count). Becoming prepared creates a copy of its
 * prepare spell ("Ancestral Craving") in exile that its controller may cast for {B}; casting that
 * copy unprepares the creature.
 */
val BloodlineRecollector = card("Bloodline Recollector") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Warlock"
    power = 2
    toughness = 2
    oracleText = "At the beginning of each end step, if three or more creatures died this turn, " +
        "this creature becomes prepared. (While it's prepared, you may cast a copy of its spell. " +
        "Doing so unprepares it.)"

    // At the beginning of each end step, if three or more creatures died this turn, it becomes prepared.
    triggeredAbility {
        trigger = Triggers.EachEndStep
        interveningIf = Conditions.CompareAmounts(
            DynamicAmounts.creaturesDiedThisTurn(Player.Each),
            ComparisonOperator.GTE,
            DynamicAmount.Fixed(3),
        )
        effect = Effects.BecomePrepared(EffectTarget.Self)
        description = "At the beginning of each end step, if three or more creatures died this " +
            "turn, this creature becomes prepared."
    }

    // Ancestral Craving — the prepare spell. Target player draws three cards and loses 3 life.
    prepare("Ancestral Craving") {
        manaCost = "{B}"
        typeLine = "Instant"
        oracleText = "Target player draws three cards and loses 3 life."
        spell {
            val player = target("target player", Targets.Player)
            effect = Effects.Composite(
                Effects.DrawCards(3, player),
                Effects.LoseLife(3, player),
            )
        }
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "49"
        artist = "Cynthia Sheppard"
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4fcc913e-f736-460a-b24b-022fa2e861b9.jpg?1788329264"
    }
}
