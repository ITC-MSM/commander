package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fusion Elemental
 * {W}{U}{B}{R}{G}
 * Creature — Elemental
 * 8/8
 *
 * Vanilla — no rules text.
 */
val FusionElemental = card("Fusion Elemental") {
    manaCost = "{W}{U}{B}{R}{G}"
    colorIdentity = "WUBRG"
    typeLine = "Creature — Elemental"
    power = 8
    toughness = 8

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "107"
        artist = "Michael Komarck"
        flavorText = "As the shards merged into the Maelstrom, their mana energies fused into new monstrosities."
        imageUri = "https://cards.scryfall.io/normal/front/c/6/c6712dbd-ee54-4fb7-93ab-64f8f07350f1.jpg?1783942469"
    }
}
