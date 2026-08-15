package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.effects.GainLifeEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Bagel and Schmear
 * {1}
 * Artifact — Food
 * Share — {W}, {T}, Sacrifice this artifact: Put a +1/+1 counter on up to one target creature. Draw a card. Activate only as a sorcery.
 * Nosh — {2}, {T}, Sacrifice this artifact: You gain 3 life and draw a card.
 */
val BagelAndSchmear = card("Bagel and Schmear") {
    manaCost = "{1}"
    colorIdentity = "W"
    typeLine = "Artifact — Food"
    oracleText = "Share — {W}, {T}, Sacrifice this artifact: Put a +1/+1 counter on up to one target creature. Draw a card. Activate only as a sorcery.\nNosh — {2}, {T}, Sacrifice this artifact: You gain 3 life and draw a card."
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W}"), Costs.Tap, Costs.SacrificeSelf)
        val t = target("target", TargetCreature(optional = true, filter = TargetFilter.Creature))
        effect = Effects.Composite(
            AddCountersEffect(counterType = Counters.PLUS_ONE_PLUS_ONE, count = 1, target = t),
            DrawCardsEffect(1)
        )
        timing = TimingRule.SorcerySpeed
    }
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.Composite(
            GainLifeEffect(3),
            DrawCardsEffect(1)
        )
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "161"
        artist = "Javier Charro"
        flavorText = "No fight in New York City was more vicious than the battle over who made the best bagels."
        imageUri = "https://cards.scryfall.io/normal/front/7/f/7f927f72-fc9b-444f-9e0e-78a5e8e7bcaa.jpg?1757377986"
    }
}
