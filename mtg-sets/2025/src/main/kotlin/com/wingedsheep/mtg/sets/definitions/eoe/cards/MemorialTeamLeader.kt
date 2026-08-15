package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Memorial Team Leader
 * {3}{R}
 * Creature — Kavu Soldier
 * During your turn, other creatures you control get +1/+0.
 * Warp {1}{R} (You may cast this card from your hand for its warp cost. Exile this creature at the beginning of the next end step, then you may cast it from exile on a later turn.)
 * 4/3
 */
val MemorialTeamLeader = card("Memorial Team Leader") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Kavu Soldier"
    oracleText = "During your turn, other creatures you control get +1/+0.\n" +
        "Warp {1}{R} (You may cast this card from your hand for its warp cost. Exile this creature at the beginning of the next end step, then you may cast it from exile on a later turn.)"
    power = 4
    toughness = 3

    staticAbility {
        condition = Conditions.IsYourTurn
        ability = ModifyStats(1, 0, GroupFilter.OtherCreaturesYouControl)
    }

    warp = "{1}{R}"

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "144"
        artist = "Andrew Mar"
        flavorText = "Recover the treasures in Kavaron's memorial vaults—by any means necessary."
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3ddc240a-62df-4773-98d7-48a9adaf1846.jpg?1752947135"
    }
}
