package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Hardened Tactician — Tarkir: Dragonstorm #191
 * {1}{W}{B} · Creature — Human Warrior · 2/4
 *
 * {1}, Sacrifice a token: Draw a card.
 *
 * The token-only sacrifice cost is the existing [GameObjectFilter.Token] filter on the
 * generic sacrifice cost — no new SDK primitive is needed (cf. Fountainport).
 */
val HardenedTactician = card("Hardened Tactician") {
    manaCost = "{1}{W}{B}"
    colorIdentity = "WB"
    typeLine = "Creature — Human Warrior"
    power = 2
    toughness = 4
    oracleText = "{1}, Sacrifice a token: Draw a card."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Sacrifice(GameObjectFilter.Token))
        effect = Effects.DrawCards(1)
        description = "{1}, Sacrifice a token: Draw a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "191"
        artist = "Milivoj Ćeran"
        flavorText = "\"Life is full of tough calls. We honor the dead today to make better decisions tomorrow.\"\n—Uruna, Mardu lieutenant"
        imageUri = "https://cards.scryfall.io/normal/front/8/6/86b225cb-5c45-4da1-a64e-b04091e483e8.jpg?1743204747"
    }
}
