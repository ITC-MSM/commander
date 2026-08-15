package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.ZoneChangeEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.effects.ManaExpiry
import com.wingedsheep.sdk.scripting.events.SpellCastPredicate
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Fire Lord Zuko
 * {R}{W}{B}
 * Legendary Creature — Human Noble Ally
 * 2/4
 *
 * Firebending X, where X is Fire Lord Zuko's power. (Whenever this creature attacks, add X {R}.
 * This mana lasts until end of combat.)
 * Whenever you cast a spell from exile and whenever a permanent you control enters from exile,
 * put a +1/+1 counter on each creature you control.
 *
 * Dynamic firebending: the `firebending(n)` DSL only models a fixed amount, so the attack trigger
 * is hand-wired as an [AddManaEffect] producing red mana equal to this creature's power
 * (`EntityProperty(Source, Power)`) with [ManaExpiry.END_OF_COMBAT] — the same firebending-style
 * mana the pool keeps through combat and discards once combat ends. The display keyword is omitted
 * because the `Firebending N` keyword ability is fixed-N only; the behavior and reminder text live
 * in the triggered ability and `oracleText`.
 *
 * The two exile payoffs are separate triggered abilities sharing the same effect: cast-from-exile
 * (`youCastSpell` gated by `SpellCastPredicate.CastFromZone(Zone.EXILE)`) and enters-from-exile
 * (a `ZoneChangeEvent` from [Zone.EXILE] to [Zone.BATTLEFIELD], `.youControl()`, ANY binding).
 * Each puts a +1/+1 counter on every creature you control via `ForEachInGroup`.
 */
val FireLordZuko = card("Fire Lord Zuko") {
    manaCost = "{R}{W}{B}"
    colorIdentity = "RWB"
    typeLine = "Legendary Creature — Human Noble Ally"
    power = 2
    toughness = 4
    oracleText = "Firebending X, where X is Fire Lord Zuko's power. (Whenever this creature attacks, " +
        "add X {R}. This mana lasts until end of combat.)\n" +
        "Whenever you cast a spell from exile and whenever a permanent you control enters from exile, " +
        "put a +1/+1 counter on each creature you control."

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = AddManaEffect(
            Color.RED,
            DynamicAmount.EntityProperty(EntityReference.Source, EntityNumericProperty.Power),
            expiry = ManaExpiry.END_OF_COMBAT,
        )
        description = "Firebending X, where X is Fire Lord Zuko's power. Whenever this creature " +
            "attacks, add X {R}. This mana lasts until end of combat."
    }

    triggeredAbility {
        trigger = Triggers.youCastSpell(
            requires = setOf(SpellCastPredicate.CastFromZone(Zone.EXILE)),
        )
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl()),
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
        )
        description = "Whenever you cast a spell from exile, put a +1/+1 counter on each creature you control."
    }

    triggeredAbility {
        trigger = TriggerSpec(
            event = ZoneChangeEvent(from = Zone.EXILE, to = Zone.BATTLEFIELD),
            binding = TriggerBinding.ANY,
        ).youControl()
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl()),
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
        )
        description = "Whenever a permanent you control enters from exile, put a +1/+1 counter on each creature you control."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "221"
        artist = "Jo Cordisco"
        flavorText = "\"Today, this war is finally over!\""
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e62d3bcc-7bb4-42be-90a9-caf3c1caa29d.jpg?1764121603"
    }
}
