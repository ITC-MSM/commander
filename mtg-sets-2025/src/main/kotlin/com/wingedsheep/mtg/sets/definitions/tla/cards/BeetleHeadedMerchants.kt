package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Beetle-Headed Merchants — Avatar: The Last Airbender #86
 * {4}{B} · Creature — Human Citizen · 5/4
 *
 * Whenever this creature attacks, you may sacrifice another creature or artifact.
 * If you do, draw a card and put a +1/+1 counter on this creature.
 *
 * The attack trigger is a [ReflexiveTriggerEffect]: the optional action is a *non-targeted*
 * `SacrificeEffect` of one creature or artifact you control excluding this creature
 * (`excludeSource = true`), and the reflexive payoff — `Effects.DrawCards(1)` then a +1/+1 counter
 * on this creature ([EffectTarget.Self]) — fires only when you actually sacrifice. The reflexive
 * executor's feasibility check skips the "may" prompt when you control no other creature/artifact,
 * so the payoff can't fire without a sacrifice. This is the rules-correct shape: the ability is not
 * targeted, so the trigger always goes on the stack and can't be fizzled by removing the fodder in
 * response (unlike a `target()`-based sacrifice).
 */
val BeetleHeadedMerchants = card("Beetle-Headed Merchants") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Citizen"
    power = 5
    toughness = 4
    oracleText = "Whenever this creature attacks, you may sacrifice another creature or artifact. " +
        "If you do, draw a card and put a +1/+1 counter on this creature."

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = ReflexiveTriggerEffect(
            action = SacrificeEffect(
                filter = GameObjectFilter.Creature.or(GameObjectFilter.Artifact),
                count = 1,
                excludeSource = true
            ),
            optional = true,
            reflexiveEffect = Effects.DrawCards(1) then
                Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
            hint = "Sacrifice another creature or artifact"
        )
        description = "Whenever this creature attacks, you may sacrifice another creature or artifact. " +
            "If you do, draw a card and put a +1/+1 counter on this creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "86"
        artist = "Norikatsu Miyoshi"
        flavorText = "To these desert-dwelling traders, there is profit to be found under every dune " +
            "of sand and in every living thing."
        imageUri = "https://cards.scryfall.io/normal/front/c/2/c2eed79f-38c1-4aac-9525-d54cb114f17f.jpg?1764120595"
    }
}
