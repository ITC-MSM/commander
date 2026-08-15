package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.effects.GainLifeEffect


/**
 * Instant Ramen
 * {2}
 * Artifact — Food
 * Flash
 * When this artifact enters, draw a card.
 * {2}, {T}, Sacrifice this artifact: You gain 3 life.
 */
val InstantRamen = card("Instant Ramen") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact — Food"
    oracleText = "Flash\nWhen this artifact enters, draw a card.\n{2}, {T}, Sacrifice this artifact: You gain 3 life."
    keywords(Keyword.FLASH)
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = DrawCardsEffect(1)
    }
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap, Costs.SacrificeSelf)
        effect = GainLifeEffect(3)
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "259"
        artist = "David Astruga"
        flavorText = "\"It's not about finding the single best ingredient. It's about crafting that perfect blend of meat, egg, and shrimp. That harmony of flavors is key.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/f/ef7011f4-fc08-4b15-973d-d15357cbe744.jpg"
    }
}
