package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Xande, Dark Mage
 * {2}{U}{B}
 * Legendary Creature — Human Wizard
 * 3/3
 * Menace
 * Xande gets +1/+1 for each noncreature, nonland card in your graveyard.
 *
 * The buff is a continuously-recomputed dynamic stat modifier scoped to the source, counting
 * the noncreature, nonland cards in the controller's graveyard.
 */
val XandeDarkMage = card("Xande, Dark Mage") {
    manaCost = "{2}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Legendary Creature — Human Wizard"
    power = 3
    toughness = 3
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)\n" +
        "Xande gets +1/+1 for each noncreature, nonland card in your graveyard."

    keywords(Keyword.MENACE)

    staticAbility {
        ability = GrantDynamicStatsEffect(
            filter = GroupFilter.source(),
            powerBonus = DynamicAmounts.zone(
                Player.You,
                Zone.GRAVEYARD,
                GameObjectFilter.Noncreature and GameObjectFilter.Nonland
            ).count(),
            toughnessBonus = DynamicAmounts.zone(
                Player.You,
                Zone.GRAVEYARD,
                GameObjectFilter.Noncreature and GameObjectFilter.Nonland
            ).count()
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "561"
        artist = "Joseph Weston"
        flavorText = "\"But your struggle was all for naught. Don't you see how close the darkness has drawn?\""
        imageUri = "https://cards.scryfall.io/normal/front/1/c/1caed9a8-b73b-470e-b9d8-8c3b7cac3eee.jpg?1748707603"
    }
}
