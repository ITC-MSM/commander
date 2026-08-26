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
 * Kazandu Blademaster
 * {W}{W}
 * Creature — Human Soldier Ally
 * 1/1
 * First strike, vigilance
 * Whenever this creature or another Ally you control enters, you may put a +1/+1 counter on this creature.
 *
 * Rally is an ability word, not a keyword: the trigger is an ANY-bound enters trigger over
 * Allies you control, so this creature's own arrival fires it alongside every later Ally.
 * The printed "you may" is the builder's `optional = true`, which lowers to a `Gate.MayDecide`
 * around the counter.
 */
val KazanduBlademaster = card("Kazandu Blademaster") {
    manaCost = "{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier Ally"
    power = 1
    toughness = 1
    oracleText = "First strike, vigilance\n" +
        "Whenever this creature or another Ally you control enters, you may put a +1/+1 counter on this creature."

    keywords(Keyword.FIRST_STRIKE, Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype("Ally").youControl(),
            binding = TriggerBinding.ANY,
        )
        optional = true
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "16"
        artist = "Michael Komarck"
        flavorText = "\"If you hire a sell-sword, you'd better watch your back. Hire me, and I'll watch it for you.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/6/9642bdbf-c03f-4c48-a5c8-c9201a08b834.jpg"
    }
}
