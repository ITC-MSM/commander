package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.GrantTriggeredAbilityEffect
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty

/**
 * The Pride of Hull Clade — Murders at Karlov Manor #172
 * {10}{G} · Legendary Creature — Crocodile Elk Turtle · 2/15
 *
 * This spell costs {X} less to cast, where X is the total toughness of creatures you control.
 * Defender
 * {2}{U}{U}: Until end of turn, target creature you control gets +1/+0, gains "Whenever this
 * creature deals combat damage to a player, draw cards equal to its toughness," and can attack as
 * though it didn't have defender.
 *
 * The cost reduction is the toughness reading of Ghalta's power reduction —
 * [CostReductionSource.TotalPropertyAmongPermanentsYouControl] is the `(property, filter)`
 * generalization of `TotalPowerYouControl`, so this is `Toughness` over creatures you control
 * rather than a bespoke source. Both read *projected* toughness, so counters and anthems count,
 * and only generic mana is reduced (CR 601.2f) — which is what the ruling "can't reduce the total
 * cost below {G}" means in practice.
 *
 * The activated ability is three separate grants onto one target, not one bundled effect: a pump,
 * a granted quoted trigger, and a defender bypass. Inside the granted ability "this creature" is
 * the *host*, not The Pride — [Triggers.DealsCombatDamageToPlayer] is SELF-bound and
 * [DynamicAmounts.sourceToughness] reads the ability's source, so both re-point at whatever
 * creature received the grant. That is what makes the card's own 15 toughness a payload you can
 * hand to something with evasion.
 *
 * The bypass is [Effects.CanAttackDespiteDefenderThisTurn] on the *target*, not on The Pride
 * itself — a 2/15 with defender is meant to hand the attack off, though targeting itself is legal
 * and is the obvious line when nothing better is on board.
 */
val ThePrideOfHullClade = card("The Pride of Hull Clade") {
    manaCost = "{10}{G}"
    colorIdentity = "GU"
    typeLine = "Legendary Creature — Crocodile Elk Turtle"
    oracleText = "This spell costs {X} less to cast, where X is the total toughness of creatures " +
        "you control.\n" +
        "Defender\n" +
        "{2}{U}{U}: Until end of turn, target creature you control gets +1/+0, gains \"Whenever " +
        "this creature deals combat damage to a player, draw cards equal to its toughness,\" and " +
        "can attack as though it didn't have defender."
    power = 2
    toughness = 15

    keywords(Keyword.DEFENDER)

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.TotalPropertyAmongPermanentsYouControl(
                    property = EntityNumericProperty.Toughness,
                    filter = GameObjectFilter.Creature,
                ),
            ),
        )
    }

    activatedAbility {
        cost = Costs.Mana("{2}{U}{U}")
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.Composite(
            Effects.ModifyStats(1, 0, creature),
            GrantTriggeredAbilityEffect(
                ability = TriggeredAbility.create(
                    trigger = Triggers.DealsCombatDamageToPlayer.event,
                    binding = Triggers.DealsCombatDamageToPlayer.binding,
                    effect = Effects.DrawCards(DynamicAmounts.sourceToughness()),
                ),
                target = creature,
            ),
            Effects.CanAttackDespiteDefenderThisTurn(creature),
        )
        description = "Until end of turn, target creature you control gets +1/+0, gains " +
            "\"Whenever this creature deals combat damage to a player, draw cards equal to its " +
            "toughness,\" and can attack as though it didn't have defender."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "172"
        artist = "Brent Hollowell"
        imageUri = "https://cards.scryfall.io/normal/front/e/d/edb40ab9-e552-4eb5-9c35-09094136dd4f.jpg?1783912862"
    }
}
