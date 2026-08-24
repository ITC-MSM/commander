package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Talon Sliver
 * {1}{W}
 * Creature — Sliver
 * 1/1
 * All Sliver creatures have first strike.
 */
val TalonSliver = card("Talon Sliver") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Sliver"
    power = 1
    toughness = 1
    oracleText = "All Sliver creatures have first strike."

    staticAbility {
        ability = GrantKeyword(Keyword.FIRST_STRIKE, GroupFilter(GameObjectFilter.Creature.withSubtype("Sliver")))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "50"
        artist = "Mike Raabe"
        flavorText = "\"Keep them at sword's length!\" Gerrard's order fell flat as each sliver's talon suddenly grew longer. \"Hold on—break out the polearms!\""
        imageUri = "https://cards.scryfall.io/normal/front/f/1/f186c4b1-b7ec-46eb-a961-257411b401b0.jpg"
    }
}
