package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Agna Qel'a
 * Land
 * This land enters tapped unless you control a basic land.
 * {T}: Add {U}.
 * {2}{U}, {T}: Draw a card, then discard a card.
 */
val AgnaQela = card("Agna Qel'a") {
    typeLine = "Land"
    colorIdentity = "U"
    oracleText = "This land enters tapped unless you control a basic land.\n" +
            "{T}: Add {U}.\n" +
            "{2}{U}, {T}: Draw a card, then discard a card."

    replacementEffect(
        EntersTapped(
            unlessCondition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.BasicLand)
        )
    )

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{U}"), Costs.Tap)
        effect = Effects.Composite(
            DrawCardsEffect(1),
            Patterns.Hand.discardCards(1)
        )
        description = "{2}{U}, {T}: Draw a card, then discard a card."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "264"
        artist = "Dom Lay"
        flavorText = "\"The frozen tundra is treacherous, the landscape itself is an icy fortress.\"\n—Admiral Zhao"
        imageUri = "https://cards.scryfall.io/normal/front/6/b/6b885829-a323-4f7d-87c9-aa4615dcbe5c.jpg?1764121943"
    }
}
