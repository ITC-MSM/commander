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
 * Makindi Shieldmate
 * {2}{W}
 * Creature — Kor Soldier Ally
 * 0/3
 * Defender
 * Whenever this creature or another Ally you control enters, you may put a +1/+1 counter on this creature.
 *
 * Rally is an ability word, not a keyword: the trigger is an ANY-bound enters trigger over
 * Allies you control, so this creature's own arrival fires it alongside every later Ally.
 * The printed "you may" is the builder's `optional = true`, which lowers to a `Gate.MayDecide`
 * around the counter.
 */
val MakindiShieldmate = card("Makindi Shieldmate") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kor Soldier Ally"
    power = 0
    toughness = 3
    oracleText = "Defender\n" +
        "Whenever this creature or another Ally you control enters, you may put a +1/+1 counter on this creature."

    keywords(Keyword.DEFENDER)

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
        collectorNumber = "26"
        artist = "Howard Lyon"
        flavorText = "The more who rely on him, the more resolute he becomes."
        imageUri = "https://cards.scryfall.io/normal/front/e/7/e76e9555-966c-4fc3-9ef4-a0154ccb8329.jpg"
    }
}
