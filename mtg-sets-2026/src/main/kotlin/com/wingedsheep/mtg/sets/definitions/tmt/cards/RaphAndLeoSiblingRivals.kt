package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.effects.TapUntapEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Raph & Leo, Sibling Rivals
 * {1}{R/W}{R/W}
 * Legendary Creature — Mutant Ninja Turtle
 * 2/4
 *
 * Whenever Raph & Leo attack, if it's the first combat phase of the
 * turn, untap one or two target attacking creatures. After this
 * phase, there is an additional combat phase.
 *
 * Mirrors the LTR Éomer, Marshal of Rohan shape — attack trigger
 * with an anti-loop limiter, untap a small group of attackers, then
 * `Effects.AddCombatPhase` to add a second combat phase (combat only,
 * no trailing main phase).
 *
 * **Approximation note:** the printed rider is *"if it's the first
 * combat phase of the turn"* (intervening-if), but the engine has no
 * "first combat phase" condition today. The same anti-infinite-loop
 * function is served by Éomer's `oncePerTurn = true` trigger-firing
 * cap; the two diverge only in a narrow edge case (another source of
 * additional combat exists *and* Raph & Leo first attack in combat
 * #2 — the intervening-if would fail, `oncePerTurn = true` would
 * still let it fire once). When a `Conditions.IsFirstCombatPhase`
 * primitive lands, swap `oncePerTurn = true` for a faithful
 * `triggerCondition`.
 */
val RaphAndLeoSiblingRivals = card("Raph & Leo, Sibling Rivals") {
    manaCost = "{1}{R/W}{R/W}"
    colorIdentity = "RW"
    typeLine = "Legendary Creature — Mutant Ninja Turtle"
    oracleText = "Whenever Raph & Leo attack, if it's the first combat phase of the turn, untap one or two target attacking creatures. After this phase, there is an additional combat phase."
    power = 2
    toughness = 4

    triggeredAbility {
        trigger = Triggers.Attacks
        oncePerTurn = true
        target(
            "one or two attacking creatures",
            TargetCreature(count = 2, minCount = 1, filter = TargetFilter.AttackingCreature)
        )
        effect = Effects.Composite(
            listOf(
                ForEachTargetEffect(
                    listOf(TapUntapEffect(EffectTarget.ContextTarget(0), tap = false))
                ),
                // "After this phase, there is an additional combat phase." (combat only — no main)
                Effects.AddCombatPhase,
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "166"
        artist = "Fajareka Setiawan"
        flavorText = "At odds as brothers, perfectly in sync as fighters."
        imageUri = "https://cards.scryfall.io/normal/front/4/9/49293f77-5d7b-4106-b485-db6ce0ed37e6.jpg?1769006383"
    }
}
