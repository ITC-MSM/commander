package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Elven Ambush
 * {3}{G}
 * Instant
 * Create a 1/1 green Elf Warrior creature token for each Elf you control.
 *
 * The token count is read at resolution from the battlefield, so the Elves counted are the ones
 * present when the spell resolves — and because the count is over Elf *permanents*, a noncreature
 * Elf would count too, exactly as the printed "each Elf you control" says.
 */
val ElvenAmbush = card("Elven Ambush") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Create a 1/1 green Elf Warrior creature token for each Elf you control."

    spell {
        effect = Effects.CreateToken(
            count = DynamicAmounts.battlefield(
                Player.You,
                GameObjectFilter.Permanent.withSubtype(Subtype.ELF)
            ).count(),
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Elf", "Warrior")
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "391"
        artist = "Chris Seaman"
        flavorText = "\"No elf can match a Tuskeri in combat. Why not charge?\"\n" +
            "—Knorjolvin, Tuskeri raider"
        imageUri = "https://cards.scryfall.io/normal/front/7/0/70479c44-da7c-48c7-8c6a-47210dc03277.jpg"
    }
}
