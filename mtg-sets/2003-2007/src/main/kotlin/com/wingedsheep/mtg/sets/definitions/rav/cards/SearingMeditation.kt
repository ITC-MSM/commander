package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect

/**
 * Searing Meditation
 * {1}{R}{W}
 * Enchantment
 * Whenever you gain life, you may pay {2}. If you do, this enchantment deals 2 damage to any target.
 *
 * [MayPayManaEffect] lowers to the `Gate.MayPay` gate — the printed "you may pay ... If you do"
 * is one gated effect, not an `optional` flag beside a separate one. The target is chosen when
 * the trigger goes on the stack, before the payment is offered.
 */
val SearingMeditation = card("Searing Meditation") {
    manaCost = "{1}{R}{W}"
    colorIdentity = "WR"
    typeLine = "Enchantment"
    oracleText = "Whenever you gain life, you may pay {2}. If you do, this enchantment deals 2 damage to any target."

    triggeredAbility {
        trigger = Triggers.YouGainLife
        val t = target("any target", Targets.Any)
        effect = MayPayManaEffect(ManaCost.parse("{2}"), Effects.DealDamage(2, t))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "226"
        artist = "Dave Dorman"
        flavorText = "\"When I meditate I see the world as it should be. All that does not fit, I remove.\"\n—Alovnek, Boros guildmage"
        imageUri = "https://cards.scryfall.io/normal/front/3/3/33c1fbdd-884d-467a-956e-7c28881002ab.jpg"
    }
}
