package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val HallwayHeckler = card("Hallway Heckler") {
    manaCost = "{2}{R}"
    colorIdentity = "BR"
    typeLine = "Creature — Elemental Sorcerer"
    power = 2
    toughness = 3
    oracleText = "This creature enters prepared. (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)\n{T}, Discard a card: Draw a card."

    keywords(Keyword.PREPARED)

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.DiscardCard)
        effect = Effects.DrawCards(1)
        description = "{T}, Discard a card: Draw a card."
    }

    prepare("Vicious Verse") {
        manaCost = "{B/R}"
        typeLine = "Sorcery"
        oracleText = "Vicious Verse deals 1 damage to target opponent."
        spell {
            val opponent = target("target opponent", Targets.Opponent)
            effect = Effects.DealDamage(1, opponent)
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "85"
        artist = "Gustavo Pelissari"
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7e324816-552f-455d-97c4-5ea6b26d2e6e.jpg?1789127576"
        inBooster = false
    }
}
