package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Oran-Rief Survivalist
 * {1}{G}
 * Creature — Human Warrior Ally
 * 1/1
 * Whenever this creature or another Ally you control enters, you may put a +1/+1 counter on this creature.
 *
 * Rally is an ability word, not a keyword: the trigger is an ANY-bound enters trigger over
 * Allies you control, so this creature's own arrival fires it alongside every later Ally.
 * The printed "you may" is the builder's `optional = true`, which lowers to a `Gate.MayDecide`
 * around the counter.
 */
val OranRiefSurvivalist = card("Oran-Rief Survivalist") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Warrior Ally"
    power = 1
    toughness = 1
    oracleText = "Whenever this creature or another Ally you control enters, you may put a +1/+1 counter on this creature."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype("Ally").youControl(),
            binding = TriggerBinding.ANY,
        )
        optional = true
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "174"
        artist = "Kev Walker"
        flavorText = "\"I'm strong enough to survive alone. I'm smart enough to want companions at my side.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/d/0d598220-bddd-48a4-8c80-eb149c6292a3.jpg"
    }
}
