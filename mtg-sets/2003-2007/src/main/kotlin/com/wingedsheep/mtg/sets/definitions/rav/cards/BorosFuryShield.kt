package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.PreventionScope
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Boros Fury-Shield — Ravnica: City of Guilds #5
 * {2}{W} · Instant
 *
 * Prevent all combat damage that would be dealt by target attacking or blocking creature this turn.
 * If {R} was spent to cast this spell, Boros Fury-Shield deals damage to that creature's controller
 * equal to the creature's power.
 *
 * The shield is directional — damage the creature *deals*, not damage dealt to it — so it is
 * `PreventAllDamageDealtBy` narrowed to [PreventionScope.CombatOnly], the same shape as Warning.
 *
 * The red rider is one of Ravnica's "if {X} was spent" clauses: the card is mono-white and the red
 * mana is a *payment* question, not a colour requirement, so a Boros land or an any-colour source
 * turns it on. `Conditions.ManaSpentToCastIncludes` reads the payment recorded on the spell, which
 * means a copy of Boros Fury-Shield — never cast, so nothing was spent for it — correctly misses
 * the rider.
 *
 * Power is read at resolution, after the shield goes up but with the creature still on the
 * battlefield, and `EffectTarget.TargetController` falls back to last-known control if it has since
 * left (CR 608.2h).
 */
val BorosFuryShield = card("Boros Fury-Shield") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Prevent all combat damage that would be dealt by target attacking or blocking " +
        "creature this turn. If {R} was spent to cast this spell, Boros Fury-Shield deals damage " +
        "to that creature's controller equal to the creature's power."

    spell {
        val creature = target("target attacking or blocking creature", TargetCreature(filter = TargetFilter.AttackingOrBlockingCreature))
        effect = Effects.PreventAllDamageDealtBy(creature, scope = PreventionScope.CombatOnly)
            .then(
                ConditionalEffect(
                    condition = Conditions.ManaSpentToCastIncludes(requiredRed = 1),
                    effect = Effects.DealDamage(
                        DynamicAmounts.targetPower(0),
                        EffectTarget.TargetController
                    )
                )
            )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "5"
        artist = "Wayne England"
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c46b2f5c-3c7a-4421-9d6f-81197e93d8d0.jpg?1783943705"
    }
}
