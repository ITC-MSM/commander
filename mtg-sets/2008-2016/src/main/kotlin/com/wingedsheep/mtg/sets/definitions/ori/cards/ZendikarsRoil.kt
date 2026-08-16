package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Zendikar's Roil
 * {3}{G}{G}
 * Enchantment
 * Landfall — Whenever a land you control enters, create a 2/2 green Elemental creature token.
 */
val ZendikarsRoil = card("Zendikar's Roil") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "Landfall — Whenever a land you control enters, create a 2/2 green Elemental creature token."

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Elemental")
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "209"
        artist = "Sam Burley"
        flavorText = "\"I was wrong. Zendikar isn't after me. It isn't after any of us. It's not evil or vengeful. It's magnificent . . . but it's in pain.\" —Nissa Revane"
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f75c0783-936d-4fa2-bdcb-01dae7a67fdb.jpg?1783938315"
    }
}
