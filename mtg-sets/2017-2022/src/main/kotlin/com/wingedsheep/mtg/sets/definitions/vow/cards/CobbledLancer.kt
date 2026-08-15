package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Cobbled Lancer
 * {U}
 * Creature — Zombie Horse
 * 3/3
 * As an additional cost to cast this spell, exile a creature card from your graveyard.
 * {3}{U}, Exile this card from your graveyard: Draw a card.
 */
val CobbledLancer = card("Cobbled Lancer") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Zombie Horse"
    oracleText = "As an additional cost to cast this spell, exile a creature card from your graveyard.\n" +
        "{3}{U}, Exile this card from your graveyard: Draw a card."
    power = 3
    toughness = 3

    additionalCost(Costs.additional.ExileCards(count = 1, filter = GameObjectFilter.Creature))

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{U}"), Costs.ExileSelf)
        effect = Effects.DrawCards(1)
        activateFromZone = Zone.GRAVEYARD
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "52"
        artist = "Igor Kieryluk"
        imageUri = "https://cards.scryfall.io/normal/front/4/a/4a2d3ba4-07c7-46bd-8241-0fa41105b771.jpg?1782703157"
    }
}
