package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Mindswipe
 * {X}{U}{R}
 * Instant
 * Counter target spell unless its controller pays {X}. Mindswipe deals X damage to that spell's controller.
 *
 * Rulings:
 * - If the controller of the target spell pays {X}, the spell won't be countered.
 *   Mindswipe will still deal damage to that player.
 * - If the target spell is an illegal target as Mindswipe tries to resolve, Mindswipe
 *   won't resolve and none of its effects will happen.
 */
val Mindswipe = card("Mindswipe") {
    manaCost = "{X}{U}{R}"
    colorIdentity = "UR"
    typeLine = "Instant"
    oracleText = "Counter target spell unless its controller pays {X}. Mindswipe deals X damage to that spell's controller."

    spell {
        target = Targets.Spell
        effect = Effects.CounterUnlessDynamicPays(DynamicAmount.XValue)
            .then(Effects.DealDamage(DynamicAmount.XValue, EffectTarget.TargetController))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "189"
        artist = "Ryan Alexander Lee"
        flavorText = "\"The past and the unwritten are frozen. To understand their meaning requires heat.\" —Arel the Whisperer"
        imageUri = "https://cards.scryfall.io/normal/front/5/5/557e8303-a021-4257-b41a-7d25f04618c8.jpg?1562786781"
        ruling("2014-09-20", "If the controller of the target spell pays {X}, the spell won't be countered. Mindswipe will still deal damage to that player.")
        ruling("2014-09-20", "If the target spell is an illegal target as Mindswipe tries to resolve (perhaps because it was countered by another spell or ability), Mindswipe won't resolve and none of its effects will happen.")
    }
}
