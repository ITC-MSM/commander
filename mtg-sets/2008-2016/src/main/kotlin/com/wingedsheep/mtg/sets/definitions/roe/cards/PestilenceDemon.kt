package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Pestilence Demon
 * {5}{B}{B}{B}
 * Creature — Demon
 * 7 / 6
 *
 * Flying
 * {B}: This creature deals 1 damage to each creature and each player.
 *
 * Modeling notes:
 *  - "Each creature and each player" is **two iterations, not one**: a group pass over every
 *    creature ([Effects.ForEachInGroup] with [EffectTarget.Self] naming the current iteration
 *    entity) and a player pass where each iteration rebinds the controller
 *    ([Effects.ForEachPlayer] over [Player.Each] with [EffectTarget.Controller]). Same idiom as
 *    Thrashing Wumpus, whose printed line is identical, and the same shape Assay compiles.
 *  - The creature filter is bare [GameObjectFilter.Creature] with no controller predicate — this
 *    hits *every* creature, including the Demon itself and your own board.
 *  - Nothing is targeted, so no `target(...)` requirement is declared; the ability can be activated
 *    with an empty battlefield.
 */
val PestilenceDemon = card("Pestilence Demon") {
    manaCost = "{5}{B}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Demon"
    power = 7
    toughness = 6
    oracleText = "Flying\n" +
        "{B}: This creature deals 1 damage to each creature and each player."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = Effects.Composite(
            Effects.ForEachInGroup(
                GroupFilter(GameObjectFilter.Creature),
                Effects.DealDamage(1, EffectTarget.Self)
            ),
            Effects.ForEachPlayer(
                Player.Each,
                listOf(Effects.DealDamage(1, EffectTarget.Controller))
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "124"
        artist = "Justin Sweet"
        flavorText = "\"I have schemed too long to be supplanted by dead gods. If I cannot have this world, no one can.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/6/a6931673-20e0-410e-bc2a-d14efa2b488a.jpg?1783941981"
    }
}
