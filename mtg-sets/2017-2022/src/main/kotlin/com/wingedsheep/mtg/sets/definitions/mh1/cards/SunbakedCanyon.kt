package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sunbaked Canyon
 * Land
 * {T}, Pay 1 life: Add {R} or {W}.
 * {1}, {T}, Sacrifice this land: Draw a card.
 *
 * One of the five Modern Horizons "horizon lands". The printed "Add {R} or {W}" line is two
 * separate mana abilities — one per colour — because each is its own activation with its own
 * cost payment; the engine has no single "add one of these two colours" mana ability.
 */
val SunbakedCanyon = card("Sunbaked Canyon") {
    manaCost = ""
    colorIdentity = "RW"
    typeLine = "Land"
    oracleText = "{T}, Pay 1 life: Add {R} or {W}.\n" +
        "{1}, {T}, Sacrifice this land: Draw a card."

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.PayLife(1))
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.PayLife(1))
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "247"
        artist = "Yeong-Hao Han"
        flavorText = "Since the river ran dry, travelers wander where fish once swam."
        imageUri = "https://cards.scryfall.io/normal/front/c/3/c36820fa-ee86-4206-9a0d-737a67cf5208.jpg?1783933065"
    }
}
