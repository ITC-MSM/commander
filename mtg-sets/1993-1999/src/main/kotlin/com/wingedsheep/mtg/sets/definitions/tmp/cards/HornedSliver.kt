package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Horned Sliver
 * {2}{G}
 * Creature — Sliver
 * 2/2
 * All Sliver creatures have trample.
 */
val HornedSliver = card("Horned Sliver") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Sliver"
    power = 2
    toughness = 2
    oracleText = "All Sliver creatures have trample."

    staticAbility {
        ability = GrantKeyword(Keyword.TRAMPLE, GroupFilter(GameObjectFilter.Creature.withSubtype("Sliver")))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "234"
        artist = "Allen Williams"
        flavorText = "A bristling wave of slivers broke against the *Weatherlight*'s timbers."
        imageUri = "https://cards.scryfall.io/normal/front/d/0/d0175cec-e64c-45c6-9208-76127e76a7cf.jpg"
    }
}
