package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.DelayedTriggerExpiry
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.events.AttackPredicate
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * The Last Ronin
 * {4}{B}{G}
 * Enchantment — Saga
 *
 * I — Destroy all creatures.
 * II — Mill four cards. When you do, return target creature card from your graveyard
 *      to your hand.
 * III — Whenever a creature you control attacks alone this turn, put three +1/+1
 *       counters on it. It gains trample, lifelink, and indestructible until end of turn.
 */
val TheLastRonin = card("The Last Ronin") {
    manaCost = "{4}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Enchantment — Saga"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)\n" +
        "I — Destroy all creatures.\n" +
        "II — Mill four cards. When you do, return target creature card from your graveyard to your hand.\n" +
        "III — Whenever a creature you control attacks alone this turn, put three +1/+1 counters on it. It gains trample, lifelink, and indestructible until end of turn."

    sagaChapter(1) {
        effect = Effects.DestroyAll(GameObjectFilter.Creature)
    }

    sagaChapter(2) {
        // Mill is mandatory, so the "when you do" reflexive always fires; its target is chosen
        // after the mill so you can grab a creature it just put into the graveyard.
        effect = ReflexiveTriggerEffect(
            action = Patterns.Library.mill(4),
            optional = false,
            reflexiveEffect = Effects.ReturnToHand(EffectTarget.ContextTarget(0)),
            reflexiveTargetRequirements = listOf(
                TargetObject(
                    filter = TargetFilter(GameObjectFilter.Creature.ownedByYou(), zone = Zone.GRAVEYARD)
                )
            )
        )
    }

    sagaChapter(3) {
        // Turn-scoped, filter-scoped delayed trigger; the per-attacker fan-out in
        // TriggerDetector.detectEventBasedDelayedTriggers binds each lone attacker to TriggeringEntity.
        effect = CreateDelayedTriggerEffect(
            trigger = Triggers.attacks(
                filter = GameObjectFilter.Creature.youControl(),
                requires = setOf(AttackPredicate.Alone),
                binding = TriggerBinding.ANY
            ),
            expiry = DelayedTriggerExpiry.EndOfTurn,
            effect = Effects.Composite(
                listOf(
                    Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 3, EffectTarget.TriggeringEntity),
                    Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.TriggeringEntity, Duration.EndOfTurn),
                    Effects.GrantKeyword(Keyword.LIFELINK, EffectTarget.TriggeringEntity, Duration.EndOfTurn),
                    Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, EffectTarget.TriggeringEntity, Duration.EndOfTurn)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "154"
        artist = "Hokyoung Kim"
        imageUri = "https://cards.scryfall.io/normal/front/7/2/72ab3ccf-3ddb-4dd1-9cfb-98802a18d954.jpg?1782135790"
    }
}
