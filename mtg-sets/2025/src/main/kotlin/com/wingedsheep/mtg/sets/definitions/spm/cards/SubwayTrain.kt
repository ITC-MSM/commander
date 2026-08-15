package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Subway Train
 * {2}
 * Artifact — Vehicle, 3/1
 * When this Vehicle enters, you may pay {G}. If you do, search your library for a basic land card,
 * reveal it, put it into your hand, then shuffle.
 * Crew 2
 */
val SubwayTrain = card("Subway Train") {
    manaCost = "{2}"
    colorIdentity = "G"
    typeLine = "Artifact — Vehicle"
    power = 3
    toughness = 1
    oracleText = "When this Vehicle enters, you may pay {G}. If you do, search your library for a basic land card, " +
        "reveal it, put it into your hand, then shuffle.\n" +
        "Crew 2 (Tap any number of creatures you control with total power 2 or more: This Vehicle becomes an artifact creature until end of turn.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{G}"),
            effect = Patterns.Library.searchLibrary(
                filter = GameObjectFilter.BasicLand,
                count = 1,
                destination = SearchDestination.HAND,
                reveal = true,
                shuffleAfter = true
            )
        )
    }

    keywordAbility(KeywordAbility.crew(2))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "178"
        artist = "Jonas De Ro"
        imageUri = "https://cards.scryfall.io/normal/front/9/6/96f869f7-db88-4580-82b7-8749a55e525c.jpg?1757378120"
    }
}
