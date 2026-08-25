package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Greenseeker
 * {G}
 * Creature — Elf Spellshaper
 * 1/1
 * {G}, {T}, Discard a card: Search your library for a basic land card, reveal it, put it into your
 * hand, then shuffle.
 */
val Greenseeker = card("Greenseeker") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Spellshaper"
    power = 1
    toughness = 1
    oracleText = "{G}, {T}, Discard a card: Search your library for a basic land card, reveal it, put it into your hand, then shuffle."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{G}"), Costs.Tap, Costs.DiscardCard)
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.BasicLand,
            destination = SearchDestination.HAND,
            reveal = true
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "198"
        artist = "Rebecca Guay"
        flavorText = "\"A rumor passes among the snakes, whispered by the rushes, inspiring the seekers: the goddess Freyalise is alive in Skyshroud.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f2dfa231-349a-4dce-bed6-c82a136fe0a1.jpg"
    }
}
