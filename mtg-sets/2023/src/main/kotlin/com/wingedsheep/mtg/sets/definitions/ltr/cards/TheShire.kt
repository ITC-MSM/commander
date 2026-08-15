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
 * The Shire
 * Legendary Land
 *
 * The Shire enters tapped unless you control a legendary creature.
 * {T}: Add {G}.
 * {1}{G}, {T}, Tap an untapped creature you control: Create a Food token.
 */
val TheShire = card("The Shire") {
    typeLine = "Legendary Land"
    colorIdentity = "G"
    oracleText = "The Shire enters tapped unless you control a legendary creature.\n{T}: Add {G}.\n" +
        "{1}{G}, {T}, Tap an untapped creature you control: Create a Food token."

    replacementEffect(
        EntersTapped(
            unlessCondition = Exists(
                player = Player.You,
                zone = Zone.BATTLEFIELD,
                filter = GameObjectFilter.Creature.legendary()
            )
        )
    )

    // {T}: Add {G}.
    activatedAbility {
        cost = Costs.Tap
        effect = AddManaEffect(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    // {1}{G}, {T}, Tap an untapped creature you control: Create a Food token.
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}{G}"),
            Costs.Tap,
            Costs.TapPermanents(
                count = 1,
                filter = GameObjectFilter.Creature.youControl()
            )
        )
        effect = Effects.CreateFood()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "260"
        artist = "Jonas De Ro"
        flavorText = "\"You must start somewhere and have some roots, and the soil of the Shire is deep.\"\n—Merry"
        imageUri = "https://cards.scryfall.io/normal/front/d/5/d5178a1b-588b-4414-a370-ac6eed51187a.jpg?1686970405"
    }
}
