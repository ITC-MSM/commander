package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dakmor Plague
 * {3}{B}{B}
 * Sorcery
 * Dakmor Plague deals 3 damage to each creature and each player.
 *
 * The Steam Blast / Magma Giant sweeper shape: a group iteration over every creature (no
 * controller predicate — both players') followed by a per-player iteration, whose body aims at
 * [EffectTarget.Controller] because [Effects.ForEachPlayer] rebinds the controller each pass.
 */
val DakmorPlague = card("Dakmor Plague") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Dakmor Plague deals 3 damage to each creature and each player."

    spell {
        effect = Effects.Composite(
            Effects.ForEachInGroup(
                GroupFilter(GameObjectFilter.Creature),
                Effects.DealDamage(3, EffectTarget.Self)
            ),
            Effects.ForEachPlayer(
                Player.Each,
                listOf(Effects.DealDamage(3, EffectTarget.Controller))
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "69"
        artist = "Jeff Miracola"
        flavorText = "The tiniest cough can be deadlier than the fiercest dragon."
        imageUri = "https://cards.scryfall.io/normal/front/5/8/58b38ef1-5839-4292-91d6-e45698c69a75.jpg"
    }
}
