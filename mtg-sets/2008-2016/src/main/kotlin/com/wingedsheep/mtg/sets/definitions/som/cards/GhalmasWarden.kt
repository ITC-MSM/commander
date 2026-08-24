package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Ghalma's Warden — Scars of Mirrodin #8
 * {3}{W} · Creature — Elephant Soldier · 2 / 4
 *
 * Metalcraft — This creature gets +2/+2 as long as you control three or more artifacts.
 *
 * "Metalcraft" is an ability word (CR 207.2c) with no rules meaning of its own — there is no
 * `Keyword.METALCRAFT`, and only the oracle line records the word. What it introduces is an
 * ordinary [ConditionalStaticAbility]: a layer-7c [ModifyStats] over [GroupFilter.source] gated by
 * [Conditions.YouControlAtLeast], recomputed at projection so the bonus comes and goes with the
 * third artifact.
 */
val GhalmasWarden = card("Ghalma's Warden") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Elephant Soldier"
    power = 2
    toughness = 4
    oracleText = "Metalcraft — This creature gets +2/+2 as long as you control three or more artifacts."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(powerBonus = 2, toughnessBonus = 2, filter = GroupFilter.source()),
            condition = Conditions.YouControlAtLeast(3, GameObjectFilter.Artifact),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "8"
        artist = "Mike Bierek"
        flavorText = "A special unit guards the loxodon artificer known as Ghalma the Shaper. They are armed and armored in her finest works of silver and steel."
        imageUri = "https://cards.scryfall.io/normal/front/e/f/efbf5ff1-6539-4116-ad4f-ce412ae20640.jpg?1783941745"
    }
}
