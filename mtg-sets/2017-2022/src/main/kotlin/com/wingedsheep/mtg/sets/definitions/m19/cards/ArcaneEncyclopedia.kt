package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Arcane Encyclopedia
 * {3}
 * Artifact — Book
 * {3}, {T}: Draw a card.
 *
 * A repeatable draw with nothing else on it: the whole card is one [Effects.DrawCards] behind a
 * [Costs.Composite] of the mana atom and [Costs.Tap], which is exactly how the printed "{3}, {T}"
 * comma reads. `Book` needs no `Subtype` constant — `TypeLine.parse` carries the word through.
 */
val ArcaneEncyclopedia = card("Arcane Encyclopedia") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact — Book"
    oracleText = "{3}, {T}: Draw a card."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}"), Costs.Tap)
        effect = Effects.DrawCards(1)
        description = "{3}, {T}: Draw a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "227"
        artist = "Nic Klein"
        flavorText = "Knowledge itself is neither good nor evil. Just as the wrong book in the wrong hands could doom all existence, the same book in the right hands could save it."
        imageUri = "https://cards.scryfall.io/normal/front/0/c/0ce4702d-f65b-413e-99da-112f632a0a63.jpg?1783934516"
    }
}
