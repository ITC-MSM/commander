package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Coral Commando
 * {2}{U}
 * Creature — Merfolk Warrior
 * 3/2
 *
 * Vanilla — no rules text.
 */
val CoralCommando = card("Coral Commando") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Warrior"
    power = 3
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "36"
        artist = "Alex Konstad"
        flavorText = "Few Ravnicans are aware of the vast reefs in their world's hidden ocean. Far beneath the great sinkholes, where the light is blue and dim, merfolk tend the coral labyrinths that feed the benthic ecosystem."
        imageUri = "https://cards.scryfall.io/normal/front/8/8/889cc2a0-d9a6-4368-92e0-055a7d7bf9d1.jpg?1783933710"
    }
}
