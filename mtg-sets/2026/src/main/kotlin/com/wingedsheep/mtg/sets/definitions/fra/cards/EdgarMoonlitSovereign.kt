package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Edgar, Moonlit Sovereign — the end-step clause is an intervening "if", so it is checked both when
 * the trigger would fire and again on resolution. "Each creature you control with a +1/+1 counter
 * on it" is the group at resolution: a creature that gets its first counter in response is included.
 */
val EdgarMoonlitSovereign = card("Edgar, Moonlit Sovereign") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Werewolf Noble"
    power = 4
    toughness = 4
    oracleText = "Flash\n" +
        "At the beginning of your end step, if you didn't cast a spell this turn, put two +1/+1 counters on Edgar.\n" +
        "{4}{G}: Put a +1/+1 counter on each creature you control with a +1/+1 counter on it."

    keywords(Keyword.FLASH)

    triggeredAbility {
        trigger = Triggers.YourEndStep
        interveningIf = Conditions.Not(Conditions.YouCastSpellsThisTurn(1))
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self)
        description = "At the beginning of your end step, if you didn't cast a spell this turn, " +
            "put two +1/+1 counters on Edgar."
    }

    activatedAbility {
        cost = Costs.Mana("{4}{G}")
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.withCounter(Counters.PLUS_ONE_PLUS_ONE).youControl()),
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
        )
        description = "{4}{G}: Put a +1/+1 counter on each creature you control with a +1/+1 counter on it."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "257"
        artist = "Joshua Raphael"
        flavorText = "Innistrad's moonlit nights belong to his pack and his pack alone."
        imageUri = "https://cards.scryfall.io/normal/front/d/a/dad6afc9-8505-4cdd-bf79-e9ba4670f2bb.jpg?1789127966"
        inBooster = false
    }
}
