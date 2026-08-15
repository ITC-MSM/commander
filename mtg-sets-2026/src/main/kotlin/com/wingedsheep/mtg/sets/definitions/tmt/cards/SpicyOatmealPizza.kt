package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Spicy Oatmeal Pizza
 * {2}{R}
 * Artifact — Food
 *
 * When this artifact enters, it deals 4 damage to any target and 3
 * damage to you.
 * {2}, {T}, Sacrifice this artifact: You gain 3 life.
 */
val SpicyOatmealPizza = card("Spicy Oatmeal Pizza") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Artifact — Food"
    oracleText = "When this artifact enters, it deals 4 damage to any target and 3 damage to you.\n{2}, {T}, Sacrifice this artifact: You gain 3 life."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val anyTarget = target("any target", Targets.Any)
        effect = Effects.DealDamage(4, anyTarget)
            .then(Effects.DealDamage(3, EffectTarget.Controller))
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}"),
            Costs.Tap,
            Costs.SacrificeSelf
        )
        effect = Effects.GainLife(3)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "109"
        artist = "Nicholas Gregory"
        flavorText = "\"Yeah, I threw in some habaneros to cut the heat on this.\"\n—Raphael"
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c4aeb45b-13ed-43ad-a366-7be10dec6222.jpg?1771342407"
    }
}
