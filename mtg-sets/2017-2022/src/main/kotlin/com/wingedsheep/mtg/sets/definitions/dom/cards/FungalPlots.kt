package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Fungal Plots
 * {1}{G}
 * Enchantment
 * {1}{G}, Exile a creature card from your graveyard: Create a 1/1 green Saproling creature token.
 * Sacrifice two Saprolings: You gain 2 life and draw a card.
 */
val FungalPlots = card("Fungal Plots") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "{1}{G}, Exile a creature card from your graveyard: Create a 1/1 green Saproling creature token.\nSacrifice two Saprolings: You gain 2 life and draw a card."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}{G}"),
            Costs.ExileFromGraveyard(1, GameObjectFilter.Creature)
        )
        effect = CreateTokenEffect(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Saproling"),
            imageUri = "https://cards.scryfall.io/normal/front/5/3/5371de1b-db33-4db4-a518-e35c71aa72b7.jpg?1562702067"
        )
    }

    activatedAbility {
        cost = Costs.SacrificeMultiple(2, GameObjectFilter.Creature.withSubtype("Saproling"))
        effect = Effects.GainLife(2)
            .then(Effects.DrawCards(1))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "160"
        artist = "Even Amundsen"
        flavorText = "Thallids nurture saprolings, entertain them, and eat them."
        imageUri = "https://cards.scryfall.io/normal/front/5/6/56419f2a-63cf-4ab7-bad0-b0f773fc0570.jpg?1562735932"
    }
}
