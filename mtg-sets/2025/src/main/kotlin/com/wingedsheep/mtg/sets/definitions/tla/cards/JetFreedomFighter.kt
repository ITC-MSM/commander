package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Jet, Freedom Fighter
 * {2}{R/W}{R/W}{R/W}
 * Legendary Creature — Human Rebel Ally
 * 3/1
 *
 * When Jet enters, he deals damage equal to the number of creatures you control to target
 * creature an opponent controls.
 * When Jet dies, put a +1/+1 counter on each of up to two target creatures.
 *
 * The enter trigger targets a single creature an opponent controls and deals damage equal to
 * [DynamicAmounts.creaturesYouControl] (counted at resolution, including Jet itself).
 * The dies trigger targets up to two creatures (any controller) as a single optional
 * [TargetCreature] (count = 2, optional), applying one +1/+1 counter to each chosen target via
 * [ForEachTargetEffect] over [EffectTarget.ContextTarget].
 */
val JetFreedomFighter = card("Jet, Freedom Fighter") {
    manaCost = "{2}{R/W}{R/W}{R/W}"
    colorIdentity = "RW"
    typeLine = "Legendary Creature — Human Rebel Ally"
    power = 3
    toughness = 1
    oracleText = "When Jet enters, he deals damage equal to the number of creatures you control " +
        "to target creature an opponent controls.\n" +
        "When Jet dies, put a +1/+1 counter on each of up to two target creatures."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target(
            "target creature an opponent controls",
            TargetCreature(filter = TargetFilter.CreatureOpponentControls)
        )
        effect = DealDamageEffect(DynamicAmounts.creaturesYouControl(), t)
        description = "When Jet enters, he deals damage equal to the number of creatures you control " +
            "to target creature an opponent controls."
    }

    triggeredAbility {
        trigger = Triggers.Dies
        target(
            "up to two target creatures",
            TargetCreature(count = 2, optional = true)
        )
        effect = ForEachTargetEffect(
            listOf(Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.ContextTarget(0)))
        )
        description = "When Jet dies, put a +1/+1 counter on each of up to two target creatures."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "229"
        artist = "Fahmi Fauzi"
        flavorText = "\"One day, we'll drive the Fire Nation out of here for good.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/2/9202c044-15e4-4218-a94d-16287ba19d69.jpg?1764121683"
    }
}
