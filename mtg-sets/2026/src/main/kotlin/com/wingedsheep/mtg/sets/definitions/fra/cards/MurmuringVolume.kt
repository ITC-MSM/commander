package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val MurmuringVolume = card("Murmuring Volume") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact — Book"
    oracleText = "{T}: Add one mana of any color.\n{2}, {T}, Discard a card: Draw a card."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana()
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap, Costs.DiscardCard)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "174"
        artist = "Zoltan Boros"
        flavorText = "Its pages surrender no words until the book has first read its prospective reader."
        imageUri = "https://cards.scryfall.io/normal/front/d/6/d68eab2e-89dd-4377-b7af-01512b1804a0.jpg?1789385977"
        inBooster = false
    }
}
