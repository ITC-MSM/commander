package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Mines of Moria
 * Legendary Land
 *
 * Mines of Moria enters tapped unless you control a legendary creature.
 * {T}: Add {R}.
 * {3}{R}, {T}, Exile three cards from your graveyard: Create two Treasure tokens.
 */
val MinesOfMoria = card("Mines of Moria") {
    typeLine = "Legendary Land"
    colorIdentity = "R"
    oracleText = "Mines of Moria enters tapped unless you control a legendary creature.\n{T}: Add {R}.\n{3}{R}, {T}, Exile three cards from your graveyard: Create two Treasure tokens."

    replacementEffect(
        EntersTapped(
            unlessCondition = Exists(
                player = Player.You,
                zone = Zone.BATTLEFIELD,
                filter = GameObjectFilter.Creature.legendary()
            )
        )
    )

    // {T}: Add {R}.
    activatedAbility {
        cost = Costs.Tap
        effect = AddManaEffect(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    // {3}{R}, {T}, Exile three cards from your graveyard: Create two Treasure tokens.
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{3}{R}"),
            Costs.Tap,
            Costs.ExileFromGraveyard(3)
        )
        effect = Effects.CreateTreasure(2)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "257"
        artist = "Arthur Yuan"
        flavorText = "\"Moria! Moria! Wonder of the Northern world!\"\n—Glóin"
        imageUri = "https://cards.scryfall.io/normal/front/0/b/0be723d6-4ada-4c3f-b87b-8ab83a4bbb8f.jpg?1686970369"
    }
}
