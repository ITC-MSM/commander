package com.wingedsheep.mtg.sets.definitions.mbs.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Quilled Slagwurm
 * {4}{G}{G}{G}
 * Creature — Phyrexian Wurm
 * 8/8
 *
 * Vanilla — no rules text.
 */
val QuilledSlagwurm = card("Quilled Slagwurm") {
    manaCost = "{4}{G}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Phyrexian Wurm"
    power = 8
    toughness = 8

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "89"
        artist = "Matt Stewart"
        flavorText = "Vorinclex removed its teeth so it wouldn't waste time chewing before moving to the next kill."
        imageUri = "https://cards.scryfall.io/normal/front/1/2/12c597b9-5024-42bd-b500-5ef6a3accda6.jpg?1783941373"
    }
}
