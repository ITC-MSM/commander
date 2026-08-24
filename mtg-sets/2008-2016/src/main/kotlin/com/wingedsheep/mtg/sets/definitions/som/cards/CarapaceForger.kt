package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Carapace Forger — Scars of Mirrodin #114
 * {1}{G} · Creature — Elf Artificer · 2 / 2
 *
 * Metalcraft — This creature gets +2/+2 as long as you control three or more artifacts.
 *
 * "Metalcraft" is an ability word (CR 207.2c) — flavour, not rules — so nothing but the oracle
 * line records it. The clause itself is an ordinary layer-7c [ConditionalStaticAbility] over
 * `GroupFilter.source()`, recomputed at projection so the bonus tracks the artifact count instead
 * of latching when the creature entered.
 */
val CarapaceForger = card("Carapace Forger") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Artificer"
    power = 2
    toughness = 2
    oracleText = "Metalcraft — This creature gets +2/+2 as long as you control three or more artifacts."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(powerBonus = 2, toughnessBonus = 2, filter = GroupFilter.source()),
            condition = Conditions.YouControlAtLeast(3, GameObjectFilter.Artifact),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "114"
        artist = "Matt Cavotta"
        flavorText = "\"Bows and whips cannot save us from these new horrors of the Mephidross.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/9/e9948e4c-d583-4fde-a305-df926cf00199.jpg?1783941719"
    }
}
