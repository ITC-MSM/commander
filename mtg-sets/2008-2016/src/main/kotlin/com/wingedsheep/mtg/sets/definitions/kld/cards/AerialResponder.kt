package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Aerial Responder
 * {1}{W}{W}
 * Creature — Dwarf Soldier
 * 2/3
 * Flying, vigilance, lifelink
 *
 * Three plain printed keywords — nothing card-specific beyond the [keywords] declaration.
 */
val AerialResponder = card("Aerial Responder") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dwarf Soldier"
    oracleText = "Flying, vigilance, lifelink"
    power = 2
    toughness = 3

    keywords(Keyword.FLYING, Keyword.VIGILANCE, Keyword.LIFELINK)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "2"
        artist = "Raoul Vitale"
        flavorText = "Dwarves have an instinct for repair, an inherent understanding of how systems work, and a reputation for fearlessness. They're perfect candidates for the Fair's emergency response team."
        imageUri = "https://cards.scryfall.io/normal/front/9/5/956adc86-c0d9-4408-837e-b5def19af1ec.jpg?1783937238"
    }
}
