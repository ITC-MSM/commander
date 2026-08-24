package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Serra's Guardian
 * {4}{W}{W}
 * Creature — Angel
 * 5/5
 * Flying (This creature can't be blocked except by creatures with flying or reach.)
 * Vigilance (Attacking doesn't cause this creature to tap.)
 * Other creatures you control have vigilance.
 */
val SerrasGuardian = card("Serra's Guardian") {
    manaCost = "{4}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    power = 5
    toughness = 5
    oracleText = "Flying (This creature can't be blocked except by creatures with flying or reach.)\n" +
        "Vigilance (Attacking doesn't cause this creature to tap.)\n" +
        "Other creatures you control have vigilance."

    keywords(Keyword.FLYING, Keyword.VIGILANCE)

    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.VIGILANCE,
            filter = GroupFilter(GameObjectFilter.Creature.youControl()).other(),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "284"
        artist = "Magali Villeneuve"
        flavorText = "She watches over the city just as Serra watches over all."
        imageUri = "https://cards.scryfall.io/normal/front/1/a/1a1d95b9-aa18-41e2-b972-93fda25e0b11.jpg"
    }
}
