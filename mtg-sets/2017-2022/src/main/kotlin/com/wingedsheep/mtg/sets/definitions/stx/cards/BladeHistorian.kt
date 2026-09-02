package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Blade Historian — Strixhaven: School of Mages #165 (canonical printing)
 * {R/W}{R/W}{R/W}{R/W} · Creature — Human Cleric · 2/3
 *
 * Attacking creatures you control have double strike.
 *
 * The Berserkers' Onslaught shape: a single Layer 6 [GrantKeyword] static whose group filter is
 * creatures you control that are attacking — the attacking state is part of the filter, so the
 * grant appears and disappears with combat rather than needing a trigger.
 */
val BladeHistorian = card("Blade Historian") {
    manaCost = "{R/W}{R/W}{R/W}{R/W}"
    colorIdentity = "RW"
    typeLine = "Creature — Human Cleric"
    oracleText =
        "Attacking creatures you control have double strike."
    power = 2
    toughness = 3

    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.DOUBLE_STRIKE,
            filter = GroupFilter(GameObjectFilter.Creature.attacking().youControl())
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "165"
        artist = "Cristi Balanescu"
        flavorText = "\"So you see, the reckless battle strategy of the Kathorran orcs was effective, but ultimately proved to be a double-edged sword. As did their double-edged swords.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/4/a46d64ec-aca4-428e-bce6-66cd755c8cc3.jpg?1783927324"
    }
}
