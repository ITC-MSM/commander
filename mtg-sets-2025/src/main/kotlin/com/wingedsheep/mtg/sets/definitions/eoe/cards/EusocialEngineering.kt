package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Eusocial Engineering
 * {3}{G}{G}
 * Enchantment
 * Landfall — Whenever a land you control enters, create a 2/2 colorless Robot artifact creature token.
 * Warp {1}{G} (You may cast this card from your hand for its warp cost. Exile this enchantment at the beginning of the next end step, then you may cast it from exile on a later turn.)
 */
val EusocialEngineering = card("Eusocial Engineering") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "Landfall — Whenever a land you control enters, create a 2/2 colorless Robot artifact creature token.\n" +
        "Warp {1}{G} (You may cast this card from your hand for its warp cost. Exile this enchantment at the beginning of the next end step, then you may cast it from exile on a later turn.)"

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = CreateTokenEffect(
            power = 2,
            toughness = 2,
            colors = setOf(), // colorless
            creatureTypes = setOf("Robot"),
            artifactToken = true,
            imageUri = "https://cards.scryfall.io/normal/front/c/4/c46f9a07-005c-44b7-8057-b2f00b274dd6.jpg?1756281130"
        )
    }

    warp = "{1}{G}"

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "181"
        artist = "Francisco Badilla"
        flavorText = "A harmonious production for a better world."
        imageUri = "https://cards.scryfall.io/normal/front/0/1/011bd7d8-6d60-482a-91b7-d3f0aad13b71.jpg?1752947291"
    }
}
