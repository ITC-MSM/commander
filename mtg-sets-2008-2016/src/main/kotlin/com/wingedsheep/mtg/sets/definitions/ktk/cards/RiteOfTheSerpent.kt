package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Rite of the Serpent
 * {4}{B}{B}
 * Sorcery
 * Destroy target creature. If that creature had a +1/+1 counter on it,
 * create a 1/1 green Snake creature token.
 *
 * Rulings:
 * - You get the Snake token no matter who controlled the target creature.
 * - Only one Snake token is created regardless of how many +1/+1 counters the creature had.
 * - If the creature isn't destroyed (e.g. indestructible), you still get the token if it has a +1/+1 counter.
 */
val RiteOfTheSerpent = card("Rite of the Serpent") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Destroy target creature. If that creature had a +1/+1 counter on it, create a 1/1 green Snake creature token."

    spell {
        // Check counter condition before destroying (counters are removed on zone change)
        effect = ConditionalEffect(
            condition = Conditions.TargetHasCounter(CounterTypeFilter.PlusOnePlusOne),
            effect = Effects.CreateToken(
                power = 1,
                toughness = 1,
                colors = setOf(Color.GREEN),
                creatureTypes = setOf("Snake"),
                imageUri = "https://cards.scryfall.io/normal/front/0/3/032e9f9d-b1e5-4724-9b80-e51500d12d5b.jpg?1562639651"
            )
        ) then Effects.Destroy(EffectTarget.ContextTarget(0))
        target = Targets.Creature
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "86"
        artist = "Seb McKinnon"
        flavorText = "From your death, new life. From your loss, our profit.\n—Kirada, Qarsi overseer"
        imageUri = "https://cards.scryfall.io/normal/front/0/0/005b9fec-66de-4079-88e0-c7de7e22d18e.jpg?1562781741"
    }
}
