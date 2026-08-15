package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bazaar of Baghdad
 * Land
 * {T}: Draw two cards, then discard three cards.
 */
val BazaarOfBaghdad = card("Bazaar of Baghdad") {
    typeLine = "Land"
    colorIdentity = ""
    oracleText = "{T}: Draw two cards, then discard three cards."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.DrawCards(2).then(Effects.Discard(3))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "70"
        artist = "Jeff A. Menges"
        imageUri = "https://cards.scryfall.io/normal/front/f/f/ff37b863-f8c4-4584-8cc2-ac0e096e583f.jpg?1562943098"
    }
}
