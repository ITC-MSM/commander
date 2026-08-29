package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Selesnya Evangel
 * {G}{W}
 * Creature — Elf Shaman
 * 1/2
 * {1}, {T}, Tap an untapped creature you control: Create a 1/1 green Saproling creature token.
 *
 * The {T} in the same cost already taps the Evangel, and an already-tapped permanent can't be
 * tapped again to pay a cost (CR 107.5) — so the tap-a-creature atom needs no `excludeSelf`.
 */
val SelesnyaEvangel = card("Selesnya Evangel") {
    manaCost = "{G}{W}"
    colorIdentity = "WG"
    typeLine = "Creature — Elf Shaman"
    oracleText = "{1}, {T}, Tap an untapped creature you control: Create a 1/1 green Saproling creature token."
    power = 1
    toughness = 2

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}"),
            Costs.Tap,
            Costs.TapPermanents(count = 1, filter = GameObjectFilter.Creature)
        )
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Saproling")
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "228"
        artist = "Rob Alexander"
        flavorText = "\"The clamor of the city drowns all voices. But together we can sing a harmony that will resonate from Ravnica's tallest spires to her deepest wells.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/8/18bc93c1-f236-4d1b-bb54-5041b3cae5a6.jpg"
    }
}
