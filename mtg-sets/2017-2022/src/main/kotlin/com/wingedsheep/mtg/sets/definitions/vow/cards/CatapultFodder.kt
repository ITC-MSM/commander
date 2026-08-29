package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.TransformEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Catapult Fodder // Catapult Captain (Innistrad: Crimson Vow)
 * {2}{B}
 * Creature — Zombie // Creature — Zombie
 *
 * Front — Catapult Fodder (1/5)
 *   At the beginning of combat on your turn, if you control three or more creatures that each have
 *   toughness greater than their power, transform this creature.
 *
 * Back — Catapult Captain (2/6)
 *   {2}{B}, {T}, Sacrifice another creature: Target opponent loses life equal to the sacrificed
 *   creature's toughness.
 *
 * The front's clause is a true intervening "if" (CR 603.4) — it gates the trigger *and* is
 * rechecked on resolution — so it rides `interveningIf` rather than wrapping the effect. Each of
 * the three creatures is measured against **its own** power, per the release-note ruling, which is
 * exactly what [ObjectFilter.toughnessGreaterThanPower] does: a per-permanent predicate counted by
 * [Conditions.YouControlAtLeast], not an aggregate comparison across the board. The Fodder itself
 * (1/5) is one of the three.
 *
 * The back reads the toughness of the creature paid as a *cost*, so the value comes from the
 * sacrifice's last-known information via [DynamicAmounts.sacrificedToughness] — the permanent is
 * already in the graveyard when the ability resolves. Same shape as Kheru Dreadmaw and Ayli,
 * Eternal Pilgrim.
 */

private val CatapultFodderFront = card("Catapult Fodder") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie"
    power = 1
    toughness = 5
    oracleText = "At the beginning of combat on your turn, if you control three or more creatures " +
        "that each have toughness greater than their power, transform this creature."

    triggeredAbility {
        trigger = Triggers.BeginCombat
        interveningIf = Conditions.YouControlAtLeast(
            3,
            GameObjectFilter.Creature.toughnessGreaterThanPower()
        )
        effect = TransformEffect(EffectTarget.Self)
        description = "At the beginning of combat on your turn, if you control three or more " +
            "creatures that each have toughness greater than their power, transform this creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "99"
        artist = "Jesper Ejsing"
        flavorText = "These days, ghouls are the only abundant resource in Thraben."
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e61b3afa-66e0-4f7b-84bc-7ae2cc6d28d4.jpg?1783924882"

        ruling(
            "2021-11-19",
            "To satisfy Catapult Fodder's condition, each of the three creatures must have " +
                "toughness greater than its own power, regardless of the power and toughness of " +
                "the other two creatures."
        )
    }
}

private val CatapultCaptain = card("Catapult Captain") {
    manaCost = ""
    colorIdentity = "B"
    colorIndicator = "B" // Transformed back face, no mana cost (CR 204).
    typeLine = "Creature — Zombie"
    power = 2
    toughness = 6
    oracleText = "{2}{B}, {T}, Sacrifice another creature: Target opponent loses life equal to the " +
        "sacrificed creature's toughness."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}{B}"),
            Costs.Tap,
            Costs.SacrificeAnother(GameObjectFilter.Creature)
        )
        val opponent = target("target opponent", Targets.Opponent)
        effect = Effects.LoseLife(DynamicAmounts.sacrificedToughness(), opponent)
        description = "{2}{B}, {T}, Sacrifice another creature: Target opponent loses life equal " +
            "to the sacrificed creature's toughness."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "99"
        artist = "Jesper Ejsing"
        flavorText = "And they're more than happy to share the wealth."
        imageUri = "https://cards.scryfall.io/normal/back/e/6/e61b3afa-66e0-4f7b-84bc-7ae2cc6d28d4.jpg?1783924882"
    }
}

val CatapultFodder: CardDefinition = CardDefinition.doubleFacedCreature(
    frontFace = CatapultFodderFront,
    backFace = CatapultCaptain,
)
