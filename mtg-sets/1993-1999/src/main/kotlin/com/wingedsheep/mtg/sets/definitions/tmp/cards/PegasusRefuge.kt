package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Pegasus Refuge
 * {3}{W}
 * Enchantment
 * {2}, Discard a card: Create a 1/1 white Pegasus creature token with flying.
 */
val PegasusRefuge = card("Pegasus Refuge") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "{2}, Discard a card: Create a 1/1 white Pegasus creature token with flying."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.DiscardCard)
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Pegasus"),
            keywords = setOf(Keyword.FLYING)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "35"
        artist = "Kev Walker"
        flavorText = "The first Rath-born pegasus was so offended by the sky that it hid its eyes in the earth.\n" +
            "—Vec lore"
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a2bce334-0ae6-4a7d-85db-99ee205ce546.jpg"
    }
}
