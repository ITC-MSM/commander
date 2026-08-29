package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Lampad of Death's Vigil
 * {1}{B}
 * Enchantment Creature — Nymph
 * 1/3
 *
 * {1}, Sacrifice a creature: Each opponent loses 1 life and you gain 1 life.
 *
 * Two separate life effects, not [Effects.DrainLife]: the printed life gain is a flat 1, not "life
 * equal to the life lost this way", so in multiplayer the Lampad drains every opponent for 1 but
 * still gains only 1. The sacrifice filter is unrestricted creatures, so the Lampad may eat itself.
 */
val LampadOfDeathsVigil = card("Lampad of Death's Vigil") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment Creature — Nymph"
    power = 1
    toughness = 3
    oracleText = "{1}, Sacrifice a creature: Each opponent loses 1 life and you gain 1 life."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Sacrifice(GameObjectFilter.Creature))
        effect = Effects.Composite(
            Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent)),
            Effects.GainLife(1),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "103"
        artist = "Jason Felix"
        flavorText = "\"Grief-struck, she weeps for each mortal's final death.\"\n—Psemilla, Meletian poet"
        imageUri = "https://cards.scryfall.io/normal/front/e/8/e8c9ada9-ea25-4a96-a4be-e4cf8f7a014f.jpg"
    }
}
