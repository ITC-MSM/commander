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
 * Nimana Sell-Sword
 * {3}{B}
 * Creature — Human Warrior Ally
 * 2/2
 * Whenever this creature or another Ally you control enters, you may put a +1/+1 counter on this creature.
 *
 * Rally is an ability word, not a keyword: the trigger is an ANY-bound enters trigger over
 * Allies you control, so this creature's own arrival fires it alongside every later Ally.
 * The printed "you may" is the builder's `optional = true`, which lowers to a `Gate.MayDecide`
 * around the counter.
 */
val NimanaSellSword = card("Nimana Sell-Sword") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Warrior Ally"
    power = 2
    toughness = 2
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
        collectorNumber = "106"
        artist = "Daarken"
        flavorText = "\"He asked if I had work for him. No wasn't the right answer.\"\n—Samila, Murasa Expeditionary House"
        imageUri = "https://cards.scryfall.io/normal/front/8/c/8c504d12-a3ef-4588-a52d-734c20f6ac58.jpg"
    }
}
