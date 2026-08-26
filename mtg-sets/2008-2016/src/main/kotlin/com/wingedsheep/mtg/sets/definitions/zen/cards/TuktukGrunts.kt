package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Tuktuk Grunts
 * {4}{R}
 * Creature — Goblin Warrior Ally
 * 2/2
 * Haste
 * Whenever this creature or another Ally you control enters, you may put a +1/+1 counter on this creature.
 *
 * Rally is an ability word, not a keyword: the trigger is an ANY-bound enters trigger over
 * Allies you control, so this creature's own arrival fires it alongside every later Ally.
 * The printed "you may" is the builder's `optional = true`, which lowers to a `Gate.MayDecide`
 * around the counter.
 */
val TuktukGrunts = card("Tuktuk Grunts") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Warrior Ally"
    power = 2
    toughness = 2
    oracleText = "Haste\n" +
        "Whenever this creature or another Ally you control enters, you may put a +1/+1 counter on this creature."

    keywords(Keyword.HASTE)

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
        collectorNumber = "152"
        artist = "Mike Bierek"
        flavorText = "Goblins are more aggressive with a crowd to cheer them on."
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e6f37982-d2ae-4ff1-b5da-3936733f8108.jpg"
    }
}
