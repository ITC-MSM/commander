package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Durkwood Boars
 * {4}{G}
 * Creature — Boar
 * 4/4
 *
 * Vanilla — no rules text.
 */
val DurkwoodBoars = card("Durkwood Boars") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Boar"
    power = 4
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "182"
        artist = "Mike Kimble"
        flavorText = "\"And the unclean spirits went out, and entered the swine; and the herd ran violently . . .\" —Mark 5:13"
        imageUri = "https://cards.scryfall.io/normal/front/8/d/8d41f08b-68fb-45f2-bdc9-488baedc7d6f.jpg?1783948049"
    }
}
