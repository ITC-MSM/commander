package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Stalwart Shield-Bearers
 * {1}{W}
 * Creature — Human Soldier
 * 0 / 3
 *
 * Defender
 * Other creatures you control with defender get +0/+2.
 *
 * Modeling notes:
 *  - A plain lord: one `staticAbility { ModifyStats(...) }` over a battlefield group filter.
 *  - Three printed words shape the filter and each earns a piece of it: "**with defender**" is
 *    `withKeyword(Keyword.DEFENDER)`, "**you control**" is `youControl()`, and "**Other**" is
 *    `.other()` — the `GroupFilter` exclude-self flag that drops the resolving source from the
 *    group, so Stalwart Shield-Bearers stays a 0/3 and does not pump itself.
 *  - Contrast Gravitational Shift in this same set, where no "you control" is printed and the
 *    filter is therefore left global. Here it is printed, so it is scoped.
 *  - Its own Defender is a plain `keywords(...)` declaration; DEFENDER is engine-live (the
 *    attack-declaration check reads it off projected keywords), so no further wiring.
 */
val StalwartShieldBearers = card("Stalwart Shield-Bearers") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 0
    toughness = 3
    oracleText = "Defender\n" +
            "Other creatures you control with defender get +0/+2."

    keywords(Keyword.DEFENDER)

    staticAbility {
        ability = ModifyStats(
            powerBonus = 0,
            toughnessBonus = 2,
            filter = GroupFilter(
                GameObjectFilter.Creature.withKeyword(Keyword.DEFENDER).youControl()
            ).other()
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "46"
        artist = "Austin Hsu"
        flavorText = "\"Hold fast the line! Either we stop them here or we wake up in their guts!\"\n—Tala Vertan, Makindi shieldmate"
        imageUri = "https://cards.scryfall.io/normal/front/8/6/868256bb-d4e2-42eb-a43a-360148aec06f.jpg?1783942002"
    }
}
