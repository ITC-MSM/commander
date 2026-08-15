package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedByCreaturesWithLessPower
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Formation Breaker
 * {1}{G}
 * Creature — Beast
 * 2/1
 *
 * Creatures with power less than this creature's power can't block it.
 * As long as you control a creature with a counter on it, this creature gets +1/+2.
 *
 * The first ability is a new attacker-side static [CantBeBlockedByCreaturesWithLessPower]
 * (the dual of Spitfire Handler's CantBlockCreaturesWithGreaterPower). The second is a
 * [ConditionalStaticAbility] wrapping a self [ModifyStats] (+1/+2), gated on
 * "you control a creature with a counter" via [Exists] + [GameObjectFilter.withAnyCounter].
 * Both comparisons use projected power, so the +1/+2 buff feeds into the evasion threshold.
 */
val FormationBreaker = card("Formation Breaker") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 2
    toughness = 1
    oracleText = "Creatures with power less than this creature's power can't block it.\n" +
        "As long as you control a creature with a counter on it, this creature gets +1/+2."

    staticAbility {
        ability = CantBeBlockedByCreaturesWithLessPower()
    }

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(1, 2, GroupFilter.source()),
            condition = Exists(
                Player.You,
                Zone.BATTLEFIELD,
                GameObjectFilter.Creature.withAnyCounter()
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "143"
        artist = "Eelis Kyttanen"
        imageUri = "https://cards.scryfall.io/normal/front/6/7/67ab8e8f-3ef6-4339-8c66-68c5aca4867a.jpg?1743207531"
    }
}
