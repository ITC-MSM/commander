package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Auriok Sunchaser — Scars of Mirrodin #4
 * {1}{W} · Creature — Human Soldier · 1 / 1
 *
 * Metalcraft — As long as you control three or more artifacts, this creature gets +2/+2 and has flying.
 *
 * "Metalcraft" is an ability word (CR 207.2c) with no rules meaning of its own — there is no
 * `Keyword.METALCRAFT`, only the oracle line records it. What the clause introduces is a pair of
 * ordinary [ConditionalStaticAbility]s over `GroupFilter.source()`: the P/T bonus in layer 7c and
 * the keyword grant in layer 6, each gated by the same artifact count. They stay two abilities
 * rather than one because the layers they apply in are different, and both are recomputed at
 * projection so the bonus and the flying appear and vanish with the third artifact.
 */
val AuriokSunchaser = card("Auriok Sunchaser") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 1
    toughness = 1
    oracleText = "Metalcraft — As long as you control three or more artifacts, this creature gets +2/+2 and has flying."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(powerBonus = 2, toughnessBonus = 2, filter = GroupFilter.source()),
            condition = Conditions.YouControlAtLeast(3, GameObjectFilter.Artifact),
        )
    }

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.FLYING, GroupFilter.source()),
            condition = Conditions.YouControlAtLeast(3, GameObjectFilter.Artifact),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "4"
        artist = "James Ryman"
        flavorText = "\"Grant me loft. Grant me light. Grant me the accuracy I need to kill all who threaten Bladehold.\"\n—Prayer to the Whitesun"
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e274a8b3-2d92-43d9-a436-d3f6f619ca95.jpg?1783941746"
    }
}
