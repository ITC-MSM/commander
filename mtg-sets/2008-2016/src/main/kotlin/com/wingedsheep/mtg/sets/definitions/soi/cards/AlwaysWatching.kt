package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Always Watching (Shadows over Innistrad #1)
 * {1}{W}{W}
 * Enchantment
 *
 * Nontoken creatures you control get +1/+1 and have vigilance.
 *
 * A two-part lord: the stat bump and the keyword grant are separate static abilities over the
 * same group ([GameObjectFilter.Creature].nontoken().youControl()), the shape Howlpack
 * Resurgence already uses.
 */
val AlwaysWatching = card("Always Watching") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Nontoken creatures you control get +1/+1 and have vigilance."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(GameObjectFilter.Creature.nontoken().youControl())
        )
    }

    staticAbility {
        ability = GrantKeyword(
            Keyword.VIGILANCE,
            GroupFilter(GameObjectFilter.Creature.nontoken().youControl())
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "1"
        artist = "Chase Stone"
        flavorText = "\"We pray to Avacyn on high. On snow-white wings fearless you fly. Keep safe our souls. Keep safe our lives. May angels watch us from the skies.\"\n—Children's prayer"
        imageUri = "https://cards.scryfall.io/normal/front/8/4/84826f49-b5fb-4bd6-ab46-98e84b0d25c8.jpg?1783937828"
    }
}
