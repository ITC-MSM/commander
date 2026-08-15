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
 * Gimli's Fury
 * {1}{R}
 * Instant
 *
 * Target creature gets +3/+2 until end of turn. If it's legendary, it also gains
 * trample until end of turn.
 */
val GimlisFury = card("Gimli's Fury") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature gets +3/+2 until end of turn. If it's legendary, it also gains trample until end of turn."

    spell {
        val creature = target("creature", Targets.Creature)
        effect = Effects.ModifyStats(+3, +2, creature)
            .then(
                ConditionalEffect(
                    condition = Conditions.TargetMatchesFilter(GameObjectFilter.Any.legendary()),
                    effect = Effects.GrantKeyword(Keyword.TRAMPLE, creature)
                )
            )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "131"
        artist = "John Di Giovanni"
        flavorText = "\"Two!\" said Gimli, patting his axe."
        imageUri = "https://cards.scryfall.io/normal/front/e/f/efde011d-8732-49b3-9204-8b114a3d81ba.jpg?1686968981"
    }
}
