package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Sliver Hivelord
 * {W}{U}{B}{R}{G}
 * Legendary Creature — Sliver
 * 5/5
 * Sliver creatures you control have indestructible.
 *
 * The Hivelord is itself a Sliver you control, so the grant covers it.
 */
val SliverHivelord = card("Sliver Hivelord") {
    manaCost = "{W}{U}{B}{R}{G}"
    colorIdentity = "BGRUW"
    typeLine = "Legendary Creature — Sliver"
    power = 5
    toughness = 5
    oracleText = "Sliver creatures you control have indestructible. (Damage and effects that say \"destroy\" don't destroy them.)"

    staticAbility {
        ability = GrantKeyword(
            Keyword.INDESTRUCTIBLE,
            GroupFilter(GameObjectFilter.Creature.withSubtype("Sliver").youControl())
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "211"
        artist = "Aleksi Briclot"
        flavorText = "\"This is the source, the line unbroken since the calamity that brought such monsters to our shores.\"\n—Hastric, Thunian scout"
        imageUri = "https://cards.scryfall.io/normal/front/b/a/ba4106de-20c7-48cf-8a36-8c6913b46c89.jpg?1783939159"
    }
}
