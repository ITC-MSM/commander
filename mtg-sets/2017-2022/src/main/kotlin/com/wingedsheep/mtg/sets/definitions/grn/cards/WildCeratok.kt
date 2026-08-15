package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wild Ceratok
 * {3}{G}
 * Creature — Rhino
 * 4/3
 *
 * Vanilla — no rules text.
 */
val WildCeratok = card("Wild Ceratok") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Rhino"
    power = 4
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "150"
        artist = "Svetlin Velinov"
        flavorText = "Once part of a wealthy merchant's private zoo, the herd roams feral throughout the Tenth, where it will remain until the guilds can agree to relocate, cull, or befriend it."
        imageUri = "https://cards.scryfall.io/normal/front/4/4/4464e11a-c5b9-40ea-8be0-dab29d14e289.jpg?1783934143"
    }
}
