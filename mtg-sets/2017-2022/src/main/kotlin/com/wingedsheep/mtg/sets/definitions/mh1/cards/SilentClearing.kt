package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Silent Clearing
 * Land
 * {T}, Pay 1 life: Add {W} or {B}.
 * {1}, {T}, Sacrifice this land: Draw a card.
 *
 * One of the five Modern Horizons "horizon lands". The printed "Add {W} or {B}" line is two
 * separate mana abilities — one per colour — because each is its own activation with its own
 * cost payment; the engine has no single "add one of these two colours" mana ability.
 */
val SilentClearing = card("Silent Clearing") {
    manaCost = ""
    colorIdentity = "BW"
    typeLine = "Land"
    oracleText = "{T}, Pay 1 life: Add {W} or {B}.\n" +
        "{1}, {T}, Sacrifice this land: Draw a card."

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.PayLife(1))
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.PayLife(1))
        effect = Effects.AddMana(Color.BLACK)
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "246"
        artist = "Seb McKinnon"
        flavorText = "The expedition's end began the marsh's story."
        imageUri = "https://cards.scryfall.io/normal/front/a/c/ac07e230-0297-4e1d-bdfe-119010e0ad8e.jpg?1783933066"
    }
}
