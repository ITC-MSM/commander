package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

val TheorixMetamage = card("Theorix Metamage") {
    manaCost = "{2}{U/B}"
    colorIdentity = "UB"
    typeLine = "Creature — Shade Wizard"
    power = 2
    toughness = 3
    oracleText = "This creature enters prepared. (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)\n" +
        "Threshold — This creature gets +1/+0 and has flying as long as there are seven or more cards in your graveyard."

    keywords(Keyword.PREPARED)

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(1, 0, GroupFilter.source()),
            condition = Conditions.CardsInGraveyardAtLeast(7)
        )
    }
    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.FLYING, GroupFilter.source()),
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
        collectorNumber = "156"
        artist = "Slawomir Maniak"
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fb6bad96-841d-4738-8e62-92f346f914fd.jpg?1789556930"
        inBooster = false
    }
}
