package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Spineseeker Centipede
 * {2}{G}
 * Creature — Insect
 * 2/1
 * When this creature enters, search your library for a basic land card, reveal it, put it into
 * your hand, then shuffle.
 * Delirium — This creature gets +1/+2 and has vigilance as long as there are four or more card
 * types among cards in your graveyard.
 */
val SpineseekerCentipede = card("Spineseeker Centipede") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Insect"
    power = 2
    toughness = 1
    oracleText = "When this creature enters, search your library for a basic land card, reveal it, put it into your hand, then shuffle.\nDelirium — This creature gets +1/+2 and has vigilance as long as there are four or more card types among cards in your graveyard."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.BasicLand,
            destination = SearchDestination.HAND,
            reveal = true
        )
    }

    // Delirium: +1/+2 while there are four or more card types among cards in your graveyard.
    staticAbility {
        ability = ModifyStats(1, 2, Filters.Self)
        condition = Conditions.Delirium()
    }

    // Delirium: vigilance while there are four or more card types among cards in your graveyard.
    staticAbility {
        ability = GrantKeyword(Keyword.VIGILANCE, Filters.Self)
        condition = Conditions.Delirium()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "199"
        artist = "Dave Kendall"
        imageUri = "https://cards.scryfall.io/normal/front/b/5/b50d697c-8358-429b-8f79-7ad9d01a5edd.jpg?1726286608"
    }
}
