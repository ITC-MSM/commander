package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cowl Prowler
 * {4}{G}{G}
 * Creature — Wurm
 * 6/6
 *
 * Vanilla — no rules text.
 */
val CowlProwler = card("Cowl Prowler") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wurm"
    power = 6
    toughness = 6

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "149"
        artist = "Tomasz Jedruszek"
        flavorText = "Consulate guards who pursue fleeing renegades into the Cowl often become lost—occasionally permanently—in the city's wildest greenbelt."
        imageUri = "https://cards.scryfall.io/normal/front/8/a/8a1b25f4-f50c-4210-a03f-080a5e4e5708.jpg?1783937183"
    }
}
