package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.scripting.effects.PayManaCostEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Spit Flame
 * {2}{R}
 * Instant
 * Spit Flame deals 4 damage to target creature.
 * Whenever a Dragon you control enters, you may pay {R}. If you do, return this card from your graveyard to your hand.
 *
 * The recursion trigger functions only from the graveyard (CR 113.6m — the ability's effect moves
 * the card out of that zone), so `triggerZones = {GRAVEYARD}`. "A Dragon you control" is not
 * "another", so the binding is [TriggerBinding.ANY], and the bare tribal noun names the subtype:
 * a noncreature Dragon permanent counts. The "you may pay {R}. If you do, ..." is a
 * [Gate.MayPay] over [PayManaCostEffect], and [Effects.ReturnToHandFromGraveyard] carries the
 * `fromZone` guard the printed line names.
 */
val SpitFlame = card("Spit Flame") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Spit Flame deals 4 damage to target creature.\n" +
        "Whenever a Dragon you control enters, you may pay {R}. If you do, return this card from your graveyard to your hand."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.DealDamage(4, t)
    }

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype(Subtype.DRAGON).youControl(),
            binding = TriggerBinding.ANY
        )
        effect = GatedEffect(
            gate = Gate.MayPay(PayManaCostEffect(ManaCost.parse("{R}"))),
            then = Effects.ReturnToHandFromGraveyard(EffectTarget.Self)
        )
        triggerZones = setOf(Zone.GRAVEYARD)
        description = "Whenever a Dragon you control enters, you may pay {R}. " +
            "If you do, return this card from your graveyard to your hand."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "160"
        artist = "Chris Rahn"
        imageUri = "https://cards.scryfall.io/normal/front/9/4/94df6198-10c0-44e7-8226-dc96d12957c4.jpg"
    }
}
