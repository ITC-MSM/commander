package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Kargan Dragonrider
 * {1}{R}
 * Creature — Human Warrior
 * 2/2
 * As long as you control a Dragon, this creature has flying.
 *
 * Canonical earliest real printing: Core Set 2019 (M19). Reprinted in Foundations (FDN).
 *
 * Modeled as a conditional static ability: GrantKeyword(FLYING, Self) gated by
 * YouControl(a Dragon). The continuous keyword grant is re-evaluated by the layer system
 * whenever the controlled-Dragon condition changes, matching "as long as".
 */
val KarganDragonrider = card("Kargan Dragonrider") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Warrior"
    power = 2
    toughness = 2
    oracleText = "As long as you control a Dragon, this creature has flying."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.FLYING, Filters.Self),
            condition = Conditions.YouControl(GameObjectFilter.Creature.withSubtype(Subtype.DRAGON))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "297"
        artist = "Greg Opalinski"
        flavorText = "\"Only those who give the dragons the respect they deserve may bear our title.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/4/34750304-c536-47d4-922d-a3654c37ffbc.jpg?1782709434"
    }
}
