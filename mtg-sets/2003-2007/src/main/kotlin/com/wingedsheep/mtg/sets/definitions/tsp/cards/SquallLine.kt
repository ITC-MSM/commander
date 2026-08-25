package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Squall Line
 * {X}{G}{G}
 * Instant
 *
 * Squall Line deals X damage to each creature with flying and each player.
 *
 * Hurricane's sentence at instant speed: one untargeted group sweep over the fliers plus a
 * per-player pass, where each iteration rebinds the controller so `EffectTarget.Controller` is the
 * player being processed.
 */
val SquallLine = card("Squall Line") {
    manaCost = "{X}{G}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Squall Line deals X damage to each creature with flying and each player."

    spell {
        effect = Effects.Composite(
            Effects.ForEachInGroup(
                GroupFilter.AllCreatures.withKeyword(Keyword.FLYING),
                Effects.DealDamage(DynamicAmount.XValue, EffectTarget.Self)
            ),
            Effects.ForEachPlayer(
                Player.Each,
                listOf(Effects.DealDamage(DynamicAmount.XValue, EffectTarget.Controller))
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "222"
        artist = "Lars Grant-West"
        flavorText = "The constant shifting of Dominaria's shredded timeline played havoc with its atmosphere, combining savage electrical storms from ages past."
        imageUri = "https://cards.scryfall.io/normal/front/3/f/3f368729-a6f2-4bf7-8b06-39c551f0b24a.jpg"
    }
}
