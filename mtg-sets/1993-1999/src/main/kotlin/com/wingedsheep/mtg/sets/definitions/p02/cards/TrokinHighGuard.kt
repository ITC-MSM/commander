package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Trokin High Guard
 * {3}{W}
 * Creature — Human Knight
 * 3/3
 *
 * Vanilla — no rules text.
 */
val TrokinHighGuard = card("Trokin High Guard") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    power = 3
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "26"
        artist = "Ron Spencer"
        flavorText = "Battle tempers soldiers as fire tempers steel."
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9c2b9302-fca6-43ca-a01c-03aec51acd0d.jpg?1783946490"
    }
}
