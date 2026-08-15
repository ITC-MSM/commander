package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.effects.BecomeCreatureEffect
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.effects.IfYouDoEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Restless Vents — LCI #284
 * Land — Rare (creature-land "Restless" cycle, mirrors Raging Ravine).
 *
 * This land enters tapped.
 * {T}: Add {B} or {R}.
 * {1}{B}{R}: Until end of turn, this land becomes a 2/3 black and red Insect creature
 *   with menace. It's still a land.
 * Whenever this land attacks, you may discard a card. If you do, draw a card.
 *
 * Note: like the other Restless lands, the attack trigger is an intrinsic (always-present)
 * triggered ability of the land itself, not one granted by the animate ability. A land can
 * only attack while it is a creature, so the trigger is effectively active only after the
 * animate ability resolves, but the ability is printed on the permanent at all times.
 *
 * The attack trigger is a rummage ("discard, then draw"), so it discards first and only draws
 * if a card was actually discarded (the "if you do" clause).
 */
val RestlessVents = card("Restless Vents") {
    typeLine = "Land"
    colorIdentity = "BR"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {B} or {R}.\n" +
        "{1}{B}{R}: Until end of turn, this land becomes a 2/3 black and red Insect creature " +
        "with menace. It's still a land.\n" +
        "Whenever this land attacks, you may discard a card. If you do, draw a card."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Mana("{1}{B}{R}")
        effect = BecomeCreatureEffect(
            target = EffectTarget.Self,
            power = DynamicAmount.Fixed(2),
            toughness = DynamicAmount.Fixed(3),
            keywords = setOf(Keyword.MENACE),
            creatureTypes = setOf("Insect"),
            colors = setOf(Color.BLACK.name, Color.RED.name),
            duration = Duration.EndOfTurn,
        )
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = MayEffect(
            effect = IfYouDoEffect(
                action = Patterns.Hand.discardCards(1),
                ifYouDo = DrawCardsEffect(1),
            ),
        )
        description = "Whenever this land attacks, you may discard a card. If you do, draw a card."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "284"
        artist = "Svetlin Velinov"
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e628e89b-bee9-408d-bb05-1784fda6b8a1.jpg?1782694384"
    }
}
