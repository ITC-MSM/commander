package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fiery Islet
 * Land
 * {T}, Pay 1 life: Add {U} or {R}.
 * {1}, {T}, Sacrifice this land: Draw a card.
 *
 * One of the five Modern Horizons "horizon lands". The printed "Add {U} or {R}" line is two
 * separate mana abilities — one per colour — because each is its own activation with its own
 * cost payment; the engine has no single "add one of these two colours" mana ability.
 */
val FieryIslet = card("Fiery Islet") {
    manaCost = ""
    colorIdentity = "RU"
    typeLine = "Land"
    oracleText = "{T}, Pay 1 life: Add {U} or {R}.\n" +
        "{1}, {T}, Sacrifice this land: Draw a card."

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.PayLife(1))
        effect = Effects.AddMana(Color.BLUE)
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.PayLife(1))
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "238"
        artist = "Richard Wright"
        flavorText = "Where water is the canvas and lava the paint."
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a3aab13c-9d9d-4507-ae5d-da979990ae1b.jpg?1783933068"
    }
}
