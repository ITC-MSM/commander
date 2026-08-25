package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nurturing Peatland
 * Land
 * {T}, Pay 1 life: Add {B} or {G}.
 * {1}, {T}, Sacrifice this land: Draw a card.
 *
 * One of the five Modern Horizons "horizon lands". The printed "Add {B} or {G}" line is two
 * separate mana abilities — one per colour — because each is its own activation with its own
 * cost payment; the engine has no single "add one of these two colours" mana ability.
 */
val NurturingPeatland = card("Nurturing Peatland") {
    manaCost = ""
    colorIdentity = "BG"
    typeLine = "Land"
    oracleText = "{T}, Pay 1 life: Add {B} or {G}.\n" +
        "{1}, {T}, Sacrifice this land: Draw a card."

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.PayLife(1))
        effect = Effects.AddMana(Color.BLACK)
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.PayLife(1))
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "243"
        artist = "Noah Bradley"
        flavorText = "New life is born within its shadows."
        imageUri = "https://cards.scryfall.io/normal/front/2/7/2744ac83-a79f-4042-8720-688b5adda382.jpg?1783933066"
    }
}
