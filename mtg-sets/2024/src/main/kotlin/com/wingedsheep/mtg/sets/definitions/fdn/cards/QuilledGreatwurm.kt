package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.MayCastSelfFromZones
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Quilled Greatwurm
 * {4}{G}{G}
 * Creature — Wurm
 * 7/7
 * Trample
 * Whenever a creature you control deals combat damage during your turn, put that many +1/+1
 *   counters on it. (It must survive to get the counters.)
 * You may cast this card from your graveyard by removing six counters from among creatures you
 *   control in addition to paying its other costs.
 *
 * Modelling notes:
 * - The counters trigger is battlefield-wide (`TriggerBinding.ANY` over
 *   `GameObjectFilter.Creature.youControl()`), not self-bound, and covers combat damage dealt to
 *   *anything* — player, planeswalker, battle or blocking creature (`RecipientFilter.Any`).
 * - "during your turn" is a fire-time gate, so it is a `triggerCondition` rather than a condition on
 *   the effect: a creature that deals combat damage on an opponent's turn never triggers at all.
 * - "put that many" reads the damage off the trigger payload
 *   (`ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT`) and "on it" is the damage source
 *   (`EffectTarget.TriggeringEntity`). The reminder text "(It must survive to get the counters.)" is
 *   just the normal resolution rule — a dead creature isn't there to receive counters.
 * - The graveyard clause is an intrinsic self-permission with a bundled additional cost:
 *   [MayCastSelfFromZones] keeps normal timing and the printed {4}{G}{G} mana cost, and the six
 *   counters may be of any type spread across any creatures you control.
 */
val QuilledGreatwurm = card("Quilled Greatwurm") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wurm"
    power = 7
    toughness = 7
    oracleText = "Trample\n" +
        "Whenever a creature you control deals combat damage during your turn, put that many +1/+1 " +
        "counters on it. (It must survive to get the counters.)\n" +
        "You may cast this card from your graveyard by removing six counters from among creatures " +
        "you control in addition to paying its other costs."

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.dealsDamage(
            damageType = DamageType.Combat,
            recipient = RecipientFilter.Any,
            sourceFilter = GameObjectFilter.Creature.youControl(),
            binding = TriggerBinding.ANY,
        )
        triggerCondition = Conditions.IsYourTurn
        effect = Effects.AddDynamicCounters(
            counterType = Counters.PLUS_ONE_PLUS_ONE,
            amount = DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT),
            target = EffectTarget.TriggeringEntity,
        )
    }

    staticAbility {
        ability = MayCastSelfFromZones(
            zones = listOf(Zone.GRAVEYARD),
            additionalCost = Costs.additional.RemoveCounters(
                count = 6,
                counterType = null,
                filter = GameObjectFilter.Creature
            )
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "111"
        artist = "Michal Ivan"
        imageUri = "https://cards.scryfall.io/normal/front/3/1/31b60531-3d33-4e66-923a-29008716b15c.jpg?1783909095"
        ruling(
            "2024-11-08",
            "You must follow all normal timing rules when casting Quilled Greatwurm from your " +
                "graveyard with the permission granted by its last ability."
        )
    }
}
