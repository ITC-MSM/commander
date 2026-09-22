package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val ProphesiedEnd = card("Prophesied End") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Destroy target creature. If it wasn't attacking, its controller draws a card."

    spell {
        val creature = target("target creature", Targets.Creature)
        // The attacking check has to read the creature while it is still on the battlefield, so the
        // branch is picked first and each branch destroys before anything else. "Its controller"
        // then resolves through last-known information.
        effect = ConditionalEffect(
            condition = Conditions.Not(Conditions.TargetMatchesFilter(GameObjectFilter.Creature.attacking())),
            effect = Effects.Composite(
                Effects.Destroy(creature),
                Effects.DrawCards(1, EffectTarget.TargetController),
            ),
            elseEffect = Effects.Destroy(creature),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "17"
        artist = "A. M. Sartor"
        flavorText = "Upon death, an oracle of Arcavios returns to the very source of their world. Jace followed Jadzi's mind to witness two planes becoming one."
        imageUri = "https://cards.scryfall.io/normal/front/1/f/1f95399a-9766-4f3d-aa6a-ece55e0530d9.jpg?1788951920"
        inBooster = false
    }
}
