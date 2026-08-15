package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Devouring Strossus
 * {5}{B}{B}{B}
 * Creature — Phyrexian Horror
 * 9/9
 * Flying, trample
 * At the beginning of your upkeep, sacrifice a creature.
 * Sacrifice a creature: Regenerate this creature.
 */
val DevouringStrossus = card("Devouring Strossus") {
    manaCost = "{5}{B}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Phyrexian Horror"
    power = 9
    toughness = 9
    oracleText = "Flying, trample\n" +
        "At the beginning of your upkeep, sacrifice a creature.\n" +
        "Sacrifice a creature: Regenerate this creature."

    keywords(Keyword.FLYING, Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.Sacrifice(
            GameObjectFilter.Creature,
            count = 1,
            target = EffectTarget.Controller
        )
    }

    activatedAbility {
        cost = Costs.Sacrifice(GameObjectFilter.Creature)
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "101"
        artist = "D. Alexander Gregory"
        imageUri = "https://cards.scryfall.io/normal/front/0/6/064f013f-e74f-419d-8d17-7748bd91885e.jpg?1562896253"
    }
}
