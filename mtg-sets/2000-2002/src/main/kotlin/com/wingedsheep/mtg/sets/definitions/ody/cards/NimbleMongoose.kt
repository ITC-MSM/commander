package com.wingedsheep.mtg.sets.definitions.ody.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Nimble Mongoose — Odyssey #258
 * {G} · Creature — Mongoose · 1 / 1
 *
 * Shroud (This creature can't be the target of spells or abilities.)
 * Threshold — This creature gets +2/+2 as long as there are seven or more cards in your graveyard.
 *
 * "Threshold" is an ability word, not a keyword — nothing in the engine reads it. It lives only in
 * [oracleText]; the ability it labels is a plain [ConditionalStaticAbility] re-evaluated during
 * layer projection, so the bonus appears and disappears the instant the graveyard crosses seven
 * cards (CR 702.21a — a static ability, never a trigger). [GroupFilter.source] scopes the
 * [ModifyStats] to this permanent only.
 */
val NimbleMongoose = card("Nimble Mongoose") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Mongoose"
    power = 1
    toughness = 1
    oracleText = "Shroud (This creature can't be the target of spells or abilities.)\n" +
        "Threshold — This creature gets +2/+2 as long as there are seven or more cards in your graveyard."

    keywords(Keyword.SHROUD)

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(powerBonus = 2, toughnessBonus = 2, filter = GroupFilter.source()),
            condition = Conditions.CardsInGraveyardAtLeast(7),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "258"
        artist = "Terese Nielsen"
        flavorText = "Faster than a cobra's bite."
        imageUri = "https://cards.scryfall.io/normal/front/9/9/99e5ecf5-a662-4df0-a6ba-9177c62b6503.jpg?1783945213"
    }
}
