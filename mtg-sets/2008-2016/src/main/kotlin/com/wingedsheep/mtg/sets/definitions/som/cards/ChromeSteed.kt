package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Chrome Steed — Scars of Mirrodin #142
 * {4} · Artifact Creature — Horse · 2 / 2
 *
 * Metalcraft — This creature gets +2/+2 as long as you control three or more artifacts.
 *
 * "Metalcraft" is an ability word (CR 207.2c): pure flavour with no rules meaning, so there is no
 * `Keyword.METALCRAFT` and only the oracle line records it. What it introduces is an ordinary
 * [ConditionalStaticAbility] — a layer-7c [ModifyStats] over [GroupFilter.source] gated by
 * [Conditions.YouControlAtLeast], recomputed at projection so the bonus appears and disappears
 * with the third artifact. The Steed counts itself, so it needs only two other artifacts.
 */
val ChromeSteed = card("Chrome Steed") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Horse"
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
        collectorNumber = "142"
        artist = "Jana Schirmer & Johannes Voss"
        flavorText = "According to Auriok myth, it collects scrap in order to reassemble its lost rider."
        imageUri = "https://cards.scryfall.io/normal/front/c/e/ce881675-690f-4d4c-a951-ab8302e904ab.jpg?1783941712"
    }
}
