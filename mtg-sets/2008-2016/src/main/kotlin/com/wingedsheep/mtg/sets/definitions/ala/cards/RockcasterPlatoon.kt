package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Rockcaster Platoon
 * {5}{W}{W}
 * Creature — Rhino Soldier
 * 5 / 7
 * {4}{G}: This creature deals 2 damage to each creature with flying and each player.
 *
 * "Each creature with flying and each player" is two iterations, not one — the Thrashing Wumpus
 * idiom. A group pass over [GroupFilter.AllCreatures]`.withKeyword(FLYING)` deals the damage to
 * [EffectTarget.Self] (the current iteration entity), and a [Effects.ForEachPlayer] pass over
 * [Player.Each] rebinds the controller each time so [EffectTarget.Controller] is the player being
 * processed. The activation cost is a plain off-colour [Costs.Mana].
 */
val RockcasterPlatoon = card("Rockcaster Platoon") {
    manaCost = "{5}{W}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Rhino Soldier"
    power = 5
    toughness = 7
    oracleText = "{4}{G}: This creature deals 2 damage to each creature with flying and each player."

    activatedAbility {
        cost = Costs.Mana("{4}{G}")
        effect = Effects.Composite(
            Effects.ForEachInGroup(
                GroupFilter.AllCreatures.withKeyword(Keyword.FLYING),
                Effects.DealDamage(2, EffectTarget.Self)
            ),
            Effects.ForEachPlayer(
                Player.Each,
                listOf(Effects.DealDamage(2, EffectTarget.Controller))
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "23"
        artist = "David Palumbo"
        flavorText = "\"Aven bandits can be sly. You've got to fill the sky with boulders—and then seek cover immediately.\"\n—Knight-Captain Wyhorn"
        imageUri = "https://cards.scryfall.io/normal/front/4/a/4ab62812-acc5-4b4b-97d3-106e399d69da.jpg"
    }
}
