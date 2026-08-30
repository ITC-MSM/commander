package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Battle Sliver
 * {4}{R}
 * Creature — Sliver
 * 3 / 3
 * Sliver creatures you control get +2/+0.
 */
val BattleSliver = card("Battle Sliver") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Sliver"
    power = 3
    toughness = 3
    oracleText = "Sliver creatures you control get +2/+0."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 2,
            toughnessBonus = 0,
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.SLIVER).youControl())
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "128"
        artist = "Slawomir Maniak"
        flavorText = "\"One emitted a strange series of buzzing clicks and guttural commands, then clawed arms emerged from all of them. Is there no limit to their adaptations?\"\n" +
            "—Hastric, Thunian scout"
        imageUri = "https://cards.scryfall.io/normal/front/6/8/68490b8c-e9d1-4f5c-9001-750be0e0569f.jpg"
    }
}
