package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Nylea's Forerunner
 * {4}{G}
 * Enchantment Creature — Beast
 * 5/3
 *
 * Trample
 * Other creatures you control have trample.
 *
 * The printed "Other" is the [GroupFilter] `excludeSelf` flag, not a predicate on the base filter —
 * the Forerunner already has trample from its own keyword, and the lord grant must not double up on
 * it. Same shape as Khenra Charioteer.
 */
val NyleasForerunner = card("Nylea's Forerunner") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment Creature — Beast"
    power = 5
    toughness = 3
    oracleText = "Trample\n" +
        "Other creatures you control have trample."

    keywords(Keyword.TRAMPLE)

    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.TRAMPLE,
            filter = GroupFilter(
                GameObjectFilter.Creature.youControl(),
                excludeSelf = true,
            ),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "186"
        artist = "Christopher Burdett"
        flavorText = "Where its feet tread, the thunder of many others will follow."
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2cf2b6be-80a8-4464-a909-8cc658196a14.jpg"
    }
}
