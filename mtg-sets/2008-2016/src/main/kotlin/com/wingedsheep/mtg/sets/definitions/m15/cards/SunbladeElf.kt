package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Sunblade Elf
 * {G}
 * Creature — Elf Warrior
 * 1/1
 * This creature gets +1/+1 as long as you control a Plains.
 * {4}{W}: Creatures you control get +1/+1 until end of turn.
 */
val SunbladeElf = card("Sunblade Elf") {
    manaCost = "{G}"
    colorIdentity = "GW"
    typeLine = "Creature — Elf Warrior"
    power = 1
    toughness = 1
    oracleText =
        "This creature gets +1/+1 as long as you control a Plains.\n" +
        "{4}{W}: Creatures you control get +1/+1 until end of turn."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(+1, +1, GroupFilter.source()),
            condition = Exists(Player.You, Zone.BATTLEFIELD, Filters.PlainsCard)
        )
    }

    activatedAbility {
        cost = Costs.Mana("{4}{W}")
        effect = Patterns.Group.modifyStatsForAll(1, 1, GroupFilter.AllCreaturesYouControl)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "202"
        artist = "Lucas Graciano"
        flavorText = "\"We patrol the steppes to keep enemies from the forest verge.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/e/5e9925c5-98d5-4eb9-8aaa-a96b63a5812d.jpg?1783939162"
    }
}
