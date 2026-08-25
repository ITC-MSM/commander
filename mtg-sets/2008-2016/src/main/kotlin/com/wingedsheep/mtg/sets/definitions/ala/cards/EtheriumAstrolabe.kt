package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Etherium Astrolabe
 * {2}{U}
 * Artifact
 * Flash
 * {B}, {T}, Sacrifice an artifact: Draw a card.
 *
 * Flash is the parameterless keyword, so it goes in `keywords(...)` rather than a
 * `keywordAbilities` entry. The ability is a three-atom [Costs.Composite] — mana, tap, and a
 * [Costs.Sacrifice] over [GameObjectFilter.Artifact]. The sacrifice filter is *not* `excludeSelf`,
 * so the Astrolabe may eat itself (it is an artifact); the tap and the sacrifice are separate
 * atoms, so it must be untapped to pay even when the thing sacrificed is something else.
 */
val EtheriumAstrolabe = card("Etherium Astrolabe") {
    manaCost = "{2}{U}"
    colorIdentity = "BU"
    typeLine = "Artifact"
    oracleText = "Flash\n" +
        "{B}, {T}, Sacrifice an artifact: Draw a card."

    keywords(Keyword.FLASH)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{B}"), Costs.Tap, Costs.Sacrifice(GameObjectFilter.Artifact))
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "41"
        artist = "Michael Bruinsma"
        flavorText = "\"Speculation is foolish when the tools of certainty are available.\"\n—Cinna, vedalken consul"
        imageUri = "https://cards.scryfall.io/normal/front/d/6/d60731c6-7a25-4f2b-8ed1-2469a2d300c6.jpg"
    }
}
