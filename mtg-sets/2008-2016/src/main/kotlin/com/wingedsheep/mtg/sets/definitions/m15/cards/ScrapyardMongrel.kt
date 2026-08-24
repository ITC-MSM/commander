package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Scrapyard Mongrel
 * {3}{R}
 * Creature — Dog
 * 3/3
 * As long as you control an artifact, this creature gets +2/+0 and has trample.
 *
 * One printed sentence, two continuous effects in different layers — the pump is Layer 7c and the
 * keyword grant is Layer 6, so each gets its own [ConditionalStaticAbility] over the same [Exists].
 */
val ScrapyardMongrel = card("Scrapyard Mongrel") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dog"
    power = 3
    toughness = 3
    oracleText = "As long as you control an artifact, this creature gets +2/+0 and has trample. (It can deal excess combat damage to the player or planeswalker it's attacking.)"

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(+2, 0, GroupFilter.source()),
            condition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Artifact)
        )
    }

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.TRAMPLE, GroupFilter.source()),
            condition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Artifact)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "160"
        artist = "Svetlin Velinov"
        flavorText = "Trespassers are welcome to try."
        imageUri = "https://cards.scryfall.io/normal/front/e/b/eb00bd3a-833e-4226-a73e-17c9043c0994.jpg?1783939170"
    }
}
