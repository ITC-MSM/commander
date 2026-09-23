package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

val VoidExtrapolator = card("Void Extrapolator") {
    manaCost = "{1}{B}"
    colorIdentity = "UB"
    typeLine = "Creature — Aetherborn Warlock"
    power = 2
    toughness = 2
    oracleText = "This creature enters prepared. (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)\n" +
        "Threshold — This creature gets +1/+1 as long as there are seven or more cards in your graveyard."

    keywords(Keyword.PREPARED)

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(1, 1, GroupFilter.source()),
            condition = Conditions.CardsInGraveyardAtLeast(7)
        )
    }

    prepare("Omit Variables") {
        manaCost = "{U/B}"
        typeLine = "Sorcery"
        oracleText = "Mill three cards. (Put the top three cards of your library into your graveyard.)"
        spell {
            effect = Patterns.Library.mill(3)
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "70"
        artist = "Valera Lutfullina"
        imageUri = "https://cards.scryfall.io/normal/front/0/e/0eae2efb-bf25-48ee-9c07-9098008110ad.jpg?1789556787"
        inBooster = false
    }
}
