package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Waterlogged Grove
 * Land
 * {T}, Pay 1 life: Add {G} or {U}.
 * {1}, {T}, Sacrifice this land: Draw a card.
 *
 * One of the five Modern Horizons "horizon lands". The printed "Add {G} or {U}" line is two
 * separate mana abilities — one per colour — because each is its own activation with its own
 * cost payment; the engine has no single "add one of these two colours" mana ability.
 */
val WaterloggedGrove = card("Waterlogged Grove") {
    manaCost = ""
    colorIdentity = "GU"
    typeLine = "Land"
    oracleText = "{T}, Pay 1 life: Add {G} or {U}.\n" +
        "{1}, {T}, Sacrifice this land: Draw a card."

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.PayLife(1))
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.PayLife(1))
        effect = Effects.AddMana(Color.BLUE)
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "249"
        artist = "John Avon"
        flavorText = "The trees pull water from deep underground, filling the forest for miles."
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0ab6bfbd-d2e1-4c4c-9f91-6f69c5b8e3bb.jpg?1783933063"
    }
}
