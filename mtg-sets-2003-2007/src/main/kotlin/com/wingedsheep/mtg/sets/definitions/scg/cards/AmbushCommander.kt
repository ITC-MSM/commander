package com.wingedsheep.mtg.sets.definitions.scg.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AnimateLandGroup
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Ambush Commander
 * {3}{G}{G}
 * Creature — Elf
 * 2/2
 * Forests you control are 1/1 green Elf creatures that are still lands.
 * {1}{G}, Sacrifice an Elf: Target creature gets +3/+3 until end of turn.
 */
val AmbushCommander = card("Ambush Commander") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf"
    power = 2
    toughness = 2
    oracleText = "Forests you control are 1/1 green Elf creatures that are still lands.\n{1}{G}, Sacrifice an Elf: Target creature gets +3/+3 until end of turn."

    staticAbility {
        ability = AnimateLandGroup(
            filter = GroupFilter(GameObjectFilter.Land.withSubtype("Forest").youControl()),
            power = 1,
            toughness = 1,
            creatureSubtypes = listOf("Elf"),
            colors = setOf(Color.GREEN)
        )
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}{G}"),
            Costs.Sacrifice(GameObjectFilter.Creature.withSubtype("Elf"))
        )
        val t = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(3, 3, t)
        description = "{1}{G}, Sacrifice an Elf: Target creature gets +3/+3 until end of turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "111"
        artist = "Darrell Riche"
        imageUri = "https://cards.scryfall.io/normal/front/7/4/7485da91-a051-4680-8a25-c81fdaa77130.jpg?1562530654"
    }
}
