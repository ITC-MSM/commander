package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Ulna Alley Shopkeep — Secrets of Strixhaven #103
 * {2}{B} · Creature — Goblin Warlock · 2/3
 *
 * Menace (This creature can't be blocked except by two or more creatures.)
 * Infusion — This creature gets +2/+0 as long as you gained life this turn.
 *
 * Infusion is an ability word (no rules meaning) — the mechanic is a conditional self-buff
 * static ability gated on [Conditions.YouGainedLifeThisTurn], applied to this creature only via
 * [GroupFilter.source].
 */
val UlnaAlleyShopkeep = card("Ulna Alley Shopkeep") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Goblin Warlock"
    power = 2
    toughness = 3
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)\n" +
        "Infusion — This creature gets +2/+0 as long as you gained life this turn."

    keywords(Keyword.MENACE)

    staticAbility {
        condition = Conditions.YouGainedLifeThisTurn
        ability = ModifyStats(powerBonus = 2, toughnessBonus = 0, filter = GroupFilter.source())
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "103"
        artist = "Inkognit"
        flavorText = "\"This one's from my private stock.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/2/c25e1ae5-f17c-4eee-98f1-5681981af31c.jpg?1775937633"
    }
}
