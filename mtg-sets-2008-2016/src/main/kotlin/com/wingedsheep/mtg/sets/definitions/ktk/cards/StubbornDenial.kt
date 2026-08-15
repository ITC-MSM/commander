package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Stubborn Denial
 * {U}
 * Instant
 * Counter target noncreature spell unless its controller pays {1}.
 * Ferocious — If you control a creature with power 4 or greater, counter that spell instead.
 */
val StubbornDenial = card("Stubborn Denial") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target noncreature spell unless its controller pays {1}.\nFerocious — If you control a creature with power 4 or greater, counter that spell instead."

    spell {
        target = Targets.NoncreatureSpell
        // Ferocious: if you control a creature with power 4+, hard counter instead
        effect = ConditionalEffect(
            condition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Creature.powerAtLeast(4)),
            effect = Effects.CounterSpell(),
            elseEffect = Effects.CounterUnlessPays("{1}")
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "56"
        artist = "James Ryman"
        flavorText = "The Temur have no patience for subtlety."
        imageUri = "https://cards.scryfall.io/normal/front/6/f/6f8626c4-306f-4e9d-8840-2bb73fe87e87.jpg?1562788344"
    }
}
