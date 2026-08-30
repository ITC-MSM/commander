package com.wingedsheep.mtg.sets.definitions.shm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Howl of the Night Pack
 * {6}{G}
 * Sorcery
 * Create a 2/2 green Wolf creature token for each Forest you control.
 *
 * One token-creation effect with a dynamic count, not a repeated one — the Forest tally is read
 * once on resolution.
 *
 * Canonical printing: Shadowmoor, the card's earliest real printing. Reprinted in Magic 2014.
 */
val HowlOfTheNightPack = card("Howl of the Night Pack") {
    manaCost = "{6}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Create a 2/2 green Wolf creature token for each Forest you control."

    spell {
        effect = Effects.CreateToken(
            count = DynamicAmounts.landsWithSubtype(Subtype.FOREST),
            power = 2,
            toughness = 2,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Wolf")
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "119"
        artist = "Lars Grant-West"
        flavorText = "The murderous horrors of Raven's Run are legendary, but even that haunted place goes quiet when the night wolves howl."
        imageUri = "https://cards.scryfall.io/normal/front/2/9/293f7768-6279-4f26-979f-ea4e48095ae5.jpg"
    }
}
