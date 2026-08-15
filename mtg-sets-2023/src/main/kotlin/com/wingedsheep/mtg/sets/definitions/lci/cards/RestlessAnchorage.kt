package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.effects.BecomeCreatureEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Restless Anchorage — LCI #280
 * Land — Rare (creature-land "Restless" cycle, mirrors Raging Ravine).
 *
 * This land enters tapped.
 * {T}: Add {W} or {U}.
 * {1}{W}{U}: Until end of turn, this land becomes a 2/3 white and blue Bird creature
 *   with flying. It's still a land.
 * Whenever this land attacks, create a Map token.
 *
 * Note: unlike Raging Ravine, the attack trigger is an intrinsic (always-present) triggered
 * ability of the land itself, not one granted by the animate ability. A land can only attack
 * while it is a creature, so the trigger is effectively active only after the animate ability
 * resolves, but the ability is printed on the permanent at all times.
 */
val RestlessAnchorage = card("Restless Anchorage") {
    typeLine = "Land"
    colorIdentity = "WU"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {W} or {U}.\n" +
        "{1}{W}{U}: Until end of turn, this land becomes a 2/3 white and blue Bird creature " +
        "with flying. It's still a land.\n" +
        "Whenever this land attacks, create a Map token."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Mana("{1}{W}{U}")
        effect = BecomeCreatureEffect(
            target = EffectTarget.Self,
            power = DynamicAmount.Fixed(2),
            toughness = DynamicAmount.Fixed(3),
            keywords = setOf(Keyword.FLYING),
            creatureTypes = setOf("Bird"),
            colors = setOf(Color.WHITE.name, Color.BLUE.name),
            duration = Duration.EndOfTurn,
        )
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.CreateMapToken()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "280"
        artist = "Leon Tukker"
        imageUri = "https://cards.scryfall.io/normal/front/e/3/e3cab352-734e-454b-8b58-733165f4b3b3.jpg?1782694389"
    }
}
