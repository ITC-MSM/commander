package com.wingedsheep.mtg.sets.definitions.fem.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vodalian Soldiers
 * {1}{U}
 * Creature — Merfolk Soldier
 * 1/2
 *
 * Vanilla — no rules text.
 */
val VodalianSoldiers = card("Vodalian Soldiers") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Soldier"
    power = 1
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "31a"
        artist = "Melissa A. Benson"
        flavorText = "\"Vodalian Soldiers had some unique advantages. Often they would ride into battle on war machines rumored to have come from the far northern oceans.\"\n—*Sarpadian Empires, vol. V*"
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7eb50256-9113-4b03-bcef-9aea24be8493.jpg?1783947907"
    }
}
