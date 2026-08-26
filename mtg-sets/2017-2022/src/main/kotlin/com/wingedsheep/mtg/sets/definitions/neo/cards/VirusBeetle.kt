package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Virus Beetle — Kamigawa: Neon Dynasty #128 (canonical printing)
 * {1}{B} · Artifact Creature — Insect · 1/1
 *
 * When this creature enters, each opponent discards a card.
 *
 * Relocated here from Edge of Eternities: NEO is the card's earliest real printing, so the
 * canonical belongs in this set and EOE carries a `Printing` row.
 */
val VirusBeetle = card("Virus Beetle") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Artifact Creature — Insect"
    power = 1
    toughness = 1
    oracleText = "When this creature enters, each opponent discards a card."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.EachOpponentDiscards(1)
        description = "When this creature enters, each opponent discards a card."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "128"
        artist = "Dan Murayama Scott"
        flavorText = "\"They'll be debugging their code for days before they figure out what " +
            "happened!\"\n—Eita, Futurist agent"
        imageUri = "https://cards.scryfall.io/normal/front/4/8/488ee202-0d28-4cc0-8a7d-644d9878e952.jpg?1783923873"
    }
}
