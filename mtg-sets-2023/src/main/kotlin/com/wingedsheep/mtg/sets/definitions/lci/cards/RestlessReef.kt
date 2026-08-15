package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
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
 * Restless Reef — LCI #282
 * Land — Rare (creature-land "Restless" cycle).
 *
 * This land enters tapped.
 * {T}: Add {U} or {B}.
 * {2}{U}{B}: Until end of turn, this land becomes a 4/4 blue and black Shark creature
 *   with deathtouch. It's still a land.
 * Whenever this land attacks, target player mills four cards.
 *
 * Note: as with the rest of the cycle, the attack trigger is an intrinsic (always-present)
 * triggered ability of the land itself, not one granted by the animate ability.
 */
val RestlessReef = card("Restless Reef") {
    typeLine = "Land"
    colorIdentity = "UB"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {U} or {B}.\n" +
        "{2}{U}{B}: Until end of turn, this land becomes a 4/4 blue and black Shark creature " +
        "with deathtouch. It's still a land.\n" +
        "Whenever this land attacks, target player mills four cards."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Mana("{2}{U}{B}")
        effect = BecomeCreatureEffect(
            target = EffectTarget.Self,
            power = DynamicAmount.Fixed(4),
            toughness = DynamicAmount.Fixed(4),
            keywords = setOf(Keyword.DEATHTOUCH),
            creatureTypes = setOf("Shark"),
            colors = setOf(Color.BLUE.name, Color.BLACK.name),
            duration = Duration.EndOfTurn,
        )
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        val player = target("target player", Targets.Player)
        effect = Patterns.Library.mill(4, player)
        description = "Whenever this land attacks, target player mills four cards."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "282"
        artist = "Hristo D. Chukov"
        imageUri = "https://cards.scryfall.io/normal/front/8/a/8a8121c9-2480-419c-aa9c-5b8b55f65014.jpg?1782694388"
    }
}
