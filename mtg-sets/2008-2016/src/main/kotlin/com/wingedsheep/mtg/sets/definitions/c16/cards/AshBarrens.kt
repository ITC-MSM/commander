package com.wingedsheep.mtg.sets.definitions.c16.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Ash Barrens
 * Land
 * {T}: Add {C}.
 * Basic landcycling {1} ({1}, Discard this card: Search your library for a basic land card, reveal it, put it into your hand, then shuffle.)
 *
 * A plain colorless mana ability plus the [KeywordAbility.basicLandcycling] alternative-play
 * ability — the engine owns the search/reveal/shuffle pipeline.
 */
val AshBarrens = card("Ash Barrens") {
    manaCost = ""
    colorIdentity = ""
    typeLine = "Land"
    oracleText = "{T}: Add {C}.\nBasic landcycling {1} ({1}, Discard this card: Search your library for a basic land card, reveal it, put it into your hand, then shuffle.)"

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    keywordAbility(KeywordAbility.basicLandcycling(ManaCost.parse("{1}")))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "56"
        artist = "Jonas De Ro"
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9d7ac112-bb20-4ea4-b797-5d21f6b7c121.jpg?1783937080"
    }
}
