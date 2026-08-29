package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Eidolon of Philosophy
 * {U}
 * Enchantment Creature — Spirit
 * 1/2
 *
 * {6}{U}, Sacrifice this creature: Draw three cards.
 *
 * A one-drop blocker that turns into a late-game card-draw spell. The sacrifice is part of the
 * activation cost, not the effect, so it is [Costs.SacrificeSelf] inside the cost composite rather
 * than a sacrifice effect: the creature is gone the moment the ability is activated, whether or not
 * the ability goes on to resolve.
 */
val EidolonOfPhilosophy = card("Eidolon of Philosophy") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Enchantment Creature — Spirit"
    power = 1
    toughness = 2
    oracleText = "{6}{U}, Sacrifice this creature: Draw three cards."

    // {6}{U}, Sacrifice this creature: Draw three cards.
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{6}{U}"),
            Costs.SacrificeSelf
        )
        effect = Effects.DrawCards(3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "48"
        artist = "Adam Paquette"
        flavorText = "\"And what did Erekastos teach us is the nature of the soul?\""
        imageUri = "https://cards.scryfall.io/normal/front/d/2/d2897635-b387-485d-932f-5655244a381f.jpg"
    }
}
