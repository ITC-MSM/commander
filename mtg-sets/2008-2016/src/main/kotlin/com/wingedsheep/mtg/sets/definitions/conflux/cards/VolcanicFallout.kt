package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Volcanic Fallout
 * {1}{R}{R}
 * Instant
 *
 * This spell can't be countered.
 * Volcanic Fallout deals 2 damage to each creature and each player.
 */
val VolcanicFallout = card("Volcanic Fallout") {
    manaCost = "{1}{R}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "This spell can't be countered.\nVolcanic Fallout deals 2 damage to each creature and each player."

    cantBeCountered = true

    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter.AllCreatures,
            DealDamageEffect(2, EffectTarget.Self),
        ).then(
            Effects.ForEachPlayer(
                Player.Each,
                listOf(DealDamageEffect(2, EffectTarget.Controller)),
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "74"
        artist = "Zoltan Boros & Gabor Szikszai"
        flavorText = "\"How can we outrun the sky?\"\n—Hadran, sunseeder of Naya"
        imageUri = "https://cards.scryfall.io/normal/front/6/5/65536d12-e75c-42b5-b592-a3ad4f550a71.jpg?1783942477"
    }
}
