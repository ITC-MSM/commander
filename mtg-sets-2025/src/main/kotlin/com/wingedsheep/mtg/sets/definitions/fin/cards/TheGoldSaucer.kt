package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.FlipCoinEffect


/**
 * The Gold Saucer
 * Land — Town
 * {T}: Add {C}.
 * {2}, {T}: Flip a coin. If you win the flip, create a Treasure token.
 * {3}, {T}, Sacrifice two artifacts: Draw a card.
 */
val TheGoldSaucer = card("The Gold Saucer") {
    manaCost = ""
    colorIdentity = ""
    typeLine = "Land — Town"
    oracleText = "{T}: Add {C}.\n" +
        "{2}, {T}: Flip a coin. If you win the flip, create a Treasure token.\n" +
        "{3}, {T}, Sacrifice two artifacts: Draw a card."
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap)
        effect = FlipCoinEffect(wonEffect = Effects.CreateTreasure(1))
    }
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{3}"),
            Costs.Tap,
            Costs.SacrificeMultiple(2, GameObjectFilter.Artifact)
        )
        effect = Effects.DrawCards(1)
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "279"
        artist = "Anthony Devine"
        flavorText = "After relaxing at Costa del Sol, stop by the Gold Saucer. It's a rich and exciting place to play!"
        imageUri = "https://cards.scryfall.io/normal/front/5/3/5363c881-443d-43df-afd8-f81e1a1741a2.jpg?1748706827"
    }
}
