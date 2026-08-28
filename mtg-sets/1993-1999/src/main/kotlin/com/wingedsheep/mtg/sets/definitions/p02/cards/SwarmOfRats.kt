package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Swarm of Rats
 * {1}{B}
 * Creature — Rat
 *
 * Swarm of Rats's power is equal to the number of Rats you control.
 *
 * A characteristic-defining ability (CR 604.3), so the starred power is `dynamicPower(...)` rather
 * than an entry in a `CardScript`. The bare tribal noun "Rats you control" is *permanents* with the
 * subtype, not creatures — a Rat artifact or enchantment would count — so the filter is
 * [GameObjectFilter.Permanent] with the subtype, not [GameObjectFilter.Creature].
 */
val SwarmOfRats = card("Swarm of Rats") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Rat"
    oracleText = "Swarm of Rats's power is equal to the number of Rats you control."
    toughness = 1

    dynamicPower(
        DynamicAmounts.battlefield(Player.You, GameObjectFilter.Permanent.withSubtype("Rat")).count()
    )

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "89"
        artist = "Kev Walker"
        imageUri = "https://cards.scryfall.io/normal/front/f/1/f154e89e-bb64-4579-9ff5-f3fc1c480105.jpg"
    }
}
