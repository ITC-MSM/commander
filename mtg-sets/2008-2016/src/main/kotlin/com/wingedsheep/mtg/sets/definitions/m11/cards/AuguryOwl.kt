package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Augury Owl
 * {1}{U}
 * Creature — Bird
 * 1/1
 * Flying
 * When this creature enters, scry 3. (Look at the top three cards of your library, then put any number of them on the bottom and the rest on top in any order.)
 *
 * [Triggers.EntersBattlefield] plus the compact [Effects.Scry] macro — the engine expands it to the
 * look/bottom/top pipeline.
 */
val AuguryOwl = card("Augury Owl") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird"
    power = 1
    toughness = 1
    oracleText = "Flying\nWhen this creature enters, scry 3. (Look at the top three cards of your library, then put any number of them on the bottom and the rest on top in any order.)"

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Scry(3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "45"
        artist = "Jim Nelson"
        imageUri = "https://cards.scryfall.io/normal/front/4/2/42b8b752-086c-4d7c-a0a2-e359819c550e.jpg?1783941827"
    }
}
