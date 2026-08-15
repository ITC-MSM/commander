package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Regal Bunnicorn
 * {1}{W}
 * Creature — Rabbit Unicorn
 * *|*
 *
 * Regal Bunnicorn's power and toughness are each equal to the number of nonland permanents you control.
 *
 * A characteristic-defining ability (CR 604.3): the P/T is set to the count of nonland permanents
 * you control in every zone, so it includes Regal Bunnicorn itself while on the battlefield.
 */
val RegalBunnicorn = card("Regal Bunnicorn") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Rabbit Unicorn"
    oracleText = "Regal Bunnicorn's power and toughness are each equal to the number of nonland permanents you control."

    dynamicStats(DynamicAmount.Count(Player.You, Zone.BATTLEFIELD, Filters.NonlandPermanent))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "25"
        artist = "Ilse Gort"
        imageUri = "https://cards.scryfall.io/normal/front/0/3/03c7d409-90e7-44d7-a8c6-4eda35fbcc83.jpg?1783915128"
    }
}
