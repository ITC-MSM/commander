package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Prakhata Club Security
 * {3}{B}
 * Creature — Aetherborn Warrior
 * 3/4
 *
 * Vanilla — no rules text.
 */
val PrakhataClubSecurity = card("Prakhata Club Security") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Aetherborn Warrior"
    power = 3
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "98"
        artist = "Igor Kieryluk"
        flavorText = "It's rare that the Prakhata Club closes its doors. It's also rare that Lord Gonti meets with Consul Kambal. What are the odds the two would occur on the same day?"
        imageUri = "https://cards.scryfall.io/normal/front/c/3/c32b73ce-cb25-4104-bccd-6b6a131790a9.jpg?1783937200"
    }
}
