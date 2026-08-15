package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ForEachPlayerEffect
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Searing Rays
 * {2}{R}
 * Sorcery
 * Choose a color. Searing Rays deals damage to each player equal to the number of
 * creatures of that color that player controls.
 */
val SearingRays = card("Searing Rays") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Choose a color. Searing Rays deals damage to each player equal to the number of " +
        "creatures of that color that player controls."

    spell {
        effect = Effects.ChooseColorThen(
            then = ForEachPlayerEffect(
                players = Player.Each,
                effects = listOf(
                    Effects.DealDamage(
                        amount = DynamicAmount.Count(
                            player = Player.You,
                            zone = Zone.BATTLEFIELD,
                            filter = GameObjectFilter(
                                cardPredicates = listOf(
                                    CardPredicate.IsCreature,
                                    CardPredicate.HasChosenColor,
                                ),
                            ),
                        ),
                        target = EffectTarget.Controller,
                    ),
                ),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "165"
        artist = "Doug Chaffee"
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4f66ff2d-f2d2-4a6b-bf26-b510de60c0b6.jpg?1562911077"
    }
}
