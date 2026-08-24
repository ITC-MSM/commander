package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Undergrowth Scavenger
 * {3}{G}
 * Creature — Fungus Horror
 * 0/0
 * This creature enters with a number of +1/+1 counters on it equal to the number of creature cards
 * in all graveyards.
 *
 * A 0/0 that only survives because of its counters — the count is taken as it enters, so it dies
 * immediately if every graveyard is creature-free.
 */
val UndergrowthScavenger = card("Undergrowth Scavenger") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Fungus Horror"
    power = 0
    toughness = 0
    oracleText = "This creature enters with a number of +1/+1 counters on it equal to the number of creature cards in all graveyards."

    replacementEffect(
        EntersWithDynamicCounters(
            count = DynamicAmounts.creatureCardsInYourGraveyard(Player.Each)
        )
    )

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "204"
        artist = "Nils Hamm"
        flavorText = "It sees a rotting carcass as a good wine which has been aged properly."
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4f8dc77e-f003-4c25-8394-cda22e3ea039.jpg?1783939160"
    }
}
