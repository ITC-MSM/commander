package com.wingedsheep.mtg.sets.definitions.eld.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.MayPayXForEffect
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Wildborn Preserver
 * {1}{G}
 * Creature — Elf Archer
 * 2/2
 * Flash
 * Reach
 * Whenever another non-Human creature you control enters, you may pay {X}. When you do, put X
 * +1/+1 counters on this creature.
 *
 * The trigger is an OTHER-bound enters trigger filtered to non-Human creatures you control
 * (`notSubtype(Human)`), so the Preserver's own arrival doesn't fire it.
 *
 * "You may pay {X}. **When you do**, …" is a reflexive triggered ability (CR 603.7): the payment
 * happens while the first ability resolves, and the counters go on the stack as a *separate*
 * ability that players may respond to. So the payment gate is [MayPayXForEffect] (a 0..max
 * number chooser that auto-taps X generic mana) and its post-payment effect is a
 * [ReflexiveTriggerEffect] with an empty `action` — the "when you do" condition is already the
 * pay-{X} gate — whose reflexive half reads the X just paid via [DynamicAmount.XValue].
 * Declining, or paying X = 0, puts no ability on the stack in any observable way.
 */
val WildbornPreserver = card("Wildborn Preserver") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Archer"
    power = 2
    toughness = 2
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)\n" +
        "Reach (This creature can block creatures with flying.)\n" +
        "Whenever another non-Human creature you control enters, you may pay {X}. When you do, " +
        "put X +1/+1 counters on this creature."

    keywords(Keyword.FLASH, Keyword.REACH)

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.youControl().notSubtype(Subtype.HUMAN),
            binding = TriggerBinding.OTHER,
        )
        effect = MayPayXForEffect(
            effect = ReflexiveTriggerEffect(
                action = Effects.Composite(emptyList()),
                optional = false,
                reflexiveEffect = Effects.AddDynamicCounters(
                    counterType = Counters.PLUS_ONE_PLUS_ONE,
                    amount = DynamicAmount.XValue,
                    target = EffectTarget.Self,
                ),
                descriptionOverride = "put X +1/+1 counters on this creature",
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "182"
        artist = "Lius Lasahido"
        imageUri = "https://cards.scryfall.io/normal/front/5/5/55f76830-369e-4224-9ded-7d1ce04c87e4.jpg?1783932600"
    }
}
