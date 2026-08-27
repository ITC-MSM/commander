package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Earth Servant
 * {5}{R}
 * Creature — Elemental
 * 4/4
 *
 * This creature gets +0/+1 for each Mountain you control.
 *
 * A Layer 7c bonus on the source itself ([GroupFilter.source]) whose toughness half recounts the
 * Mountains you control continuously — not a snapshot, so it tracks lands entering and leaving.
 */
val EarthServant = card("Earth Servant") {
    manaCost = "{5}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental"
    power = 4
    toughness = 4
    oracleText = "This creature gets +0/+1 for each Mountain you control."

    staticAbility {
        ability = GrantDynamicStatsEffect(
            filter = GroupFilter.source(),
            powerBonus = DynamicAmount.Fixed(0),
            toughnessBonus = DynamicAmount.AggregateBattlefield(
                Player.You,
                GameObjectFilter.Land.withSubtype(Subtype.MOUNTAIN)
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "134"
        artist = "Lucas Graciano"
        flavorText = "\"Fire. Air. Water. These elements spread, filling in the spaces they are not. But earth is implacable like a sullen child.\"\n" +
            "—Jestus Dreya, *Of Elements and Eternity*"
        imageUri = "https://cards.scryfall.io/normal/front/6/0/6007db49-f750-43c6-ac09-86a61efc1bb2.jpg?1783941807"
    }
}
