package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Helldozer
 * {3}{B}{B}{B}
 * Creature — Zombie Giant
 * 6/5
 *
 * {B}{B}{B}, {T}: Destroy target land. If that land was nonbasic, untap this creature.
 *
 * Same ordering as Molten Rain and Choking Sands: run the conditional rider while the target is
 * still on the battlefield, then destroy. The condition reads the target's *current* nonbasic
 * status, which is what the past-tense oracle phrasing ("if that land **was** nonbasic") means —
 * after the move the target no longer names a battlefield permanent, so a check on the far side
 * would silently answer "no" and the untap would never fire.
 *
 * The rider is an untap, not damage, and that is what makes the ability repeatable within a turn:
 * untapping refunds the {T} in the cost, so with enough black mana Helldozer eats every nonbasic
 * land on the board. Note the ability says "was nonbasic", not "was destroyed" — a nonbasic land
 * that regenerates or is indestructible still untaps Helldozer, which this ordering gets right for
 * free.
 */
val Helldozer = card("Helldozer") {
    manaCost = "{3}{B}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Giant"
    oracleText = "{B}{B}{B}, {T}: Destroy target land. If that land was nonbasic, untap this creature."
    power = 6
    toughness = 5

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{B}{B}{B}"), Costs.Tap)
        target = Targets.Land
        effect = ConditionalEffect(
            condition = Conditions.TargetMatchesFilter(GameObjectFilter.NonbasicLand),
            effect = Effects.Untap(EffectTarget.Self)
        ) then Effects.Move(EffectTarget.ContextTarget(0), Zone.GRAVEYARD, byDestruction = true)
        description = "Destroy target land. If that land was nonbasic, untap this creature."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "88"
        artist = "Zoltan Boros & Gabor Szikszai"
        flavorText = "Sometimes you go to hell, and sometimes hell comes to you."
        imageUri = "https://cards.scryfall.io/normal/front/2/f/2ff5be07-166c-4b51-9ca5-655e21c32370.jpg?1783943669"
    }
}
