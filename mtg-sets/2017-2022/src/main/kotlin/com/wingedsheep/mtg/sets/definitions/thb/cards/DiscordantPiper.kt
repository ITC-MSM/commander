package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Discordant Piper
 * {1}{B}
 * Creature — Zombie Satyr
 * 2/1
 *
 * When this creature dies, create a 0/1 white Goat creature token.
 *
 * A plain [Triggers.Dies] over a single [Effects.CreateToken]. No `triggerZone` — setting one
 * replaces the default `{BATTLEFIELD}` and the trigger is then never indexed. THB ships the Goat
 * token art, so the token resolves its image from the set's own printing.
 */
val DiscordantPiper = card("Discordant Piper") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Satyr"
    power = 2
    toughness = 1
    oracleText = "When this creature dies, create a 0/1 white Goat creature token."

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.CreateToken(
            power = 0,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Goat"),
        )
        description = "When this creature dies, create a 0/1 white Goat creature token."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "88"
        artist = "John Stanko"
        flavorText = "The death of the party."
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a8cce294-f6ee-4b18-8b65-7d01d0317b00.jpg"
    }
}
