package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Spider-Bot
 * {2}
 * Artifact Creature — Spider Robot Scout, 2/1
 * Reach
 * When this creature enters, you may search your library for a basic land card, reveal it, then shuffle and put that card on top.
 */
val SpiderBot = card("Spider-Bot") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Spider Robot Scout"
    oracleText = "Reach\nWhen this creature enters, you may search your library for a basic land card, reveal it, then shuffle and put that card on top."
    power = 2
    toughness = 1
    keywords(Keyword.REACH)
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.BasicLand,
            destination = SearchDestination.TOP_OF_LIBRARY,
            reveal = true
        )
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "173"
        artist = "Carlos Dattoli"
        flavorText = "\"Parker was a fool to waste his time on patrol. I'll have my spider-bots do it for me.\"\n—Superior Spider-Man, Otto Octavius"
        imageUri = "https://cards.scryfall.io/normal/front/2/4/24df824a-c1d6-4f09-b866-313b31fec5fb.jpg?1757378080"
    }
}
