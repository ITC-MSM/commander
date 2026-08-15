package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Rush the Room
 * {R}
 * Instant
 *
 * Target creature gets +1/+0 and gains first strike until end of turn. If it's a Goblin
 * or Orc, it also gains haste until end of turn.
 */
val RushTheRoom = card("Rush the Room") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature gets +1/+0 and gains first strike until end of turn. If it's a Goblin " +
        "or Orc, it also gains haste until end of turn."

    spell {
        val creature = target("creature", Targets.Creature)
        effect = Effects.ModifyStats(1, 0, creature)
            .then(Effects.GrantKeyword(Keyword.FIRST_STRIKE, creature))
            .then(
                ConditionalEffect(
                    condition = Conditions.TargetMatchesFilter(GameObjectFilter.Creature.withAnySubtype("Goblin", "Orc")),
                    effect = Effects.GrantKeyword(Keyword.HASTE, creature)
                )
            )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "147"
        artist = "Warren Mahy"
        flavorText = "There was a horn-blast and a rush of feet, and Orcs one after another leaped into the chamber."
        imageUri = "https://cards.scryfall.io/normal/front/f/5/f525b727-acde-427b-9c33-20964e8cf613.jpg?1686969162"
    }
}
