package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CompositeStaticAbility
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Boggart Sprite-Chaser
 * {1}{R}
 * Creature — Goblin Warrior
 * 1/2
 * As long as you control a Faerie, this creature gets +1/+1 and has flying.
 *
 * One printed ability spanning two layers (P/T in 7c, keywords in 6), so the two grants are bundled
 * in a [CompositeStaticAbility] under one [ConditionalStaticAbility] rather than authored as two
 * independent statics — CR 613.6.
 */
val BoggartSpriteChaser = card("Boggart Sprite-Chaser") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Warrior"
    power = 1
    toughness = 2
    oracleText = "As long as you control a Faerie, this creature gets +1/+1 and has flying."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = CompositeStaticAbility(
                listOf(
                    ModifyStats(powerBonus = 1, toughnessBonus = 1, filter = GroupFilter.source()),
                    GrantKeyword(Keyword.FLYING, GroupFilter.source())
                )
            ),
            condition = Conditions.ControlPermanentOfType(Subtype.FAERIE)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "156"
        artist = "Mark Poole"
        flavorText = "\"Auntie pointed out to the faerie how much mischief a flying boggart could wreak, and a beautiful new friendship was born.\"\n—A tale of Auntie Grub"
        imageUri = "https://cards.scryfall.io/normal/front/6/3/6341b73b-a035-45a2-908d-879c8eed4bbd.jpg?1783942880"
    }
}
