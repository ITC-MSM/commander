package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Steer Clear
 * {W}
 * Instant
 *
 * Steer Clear deals 2 damage to target attacking or blocking creature. Steer Clear deals 4 damage
 * to that creature instead if you controlled a Mount as you cast this spell.
 *
 * "As you cast this spell" is a cast-time condition capture (CR 601.2i): whether you controlled a
 * Mount is locked in the moment the spell is cast and read back at resolution, so losing the Mount
 * before resolution doesn't drop the damage to 2 (per the OTJ ruling). Modeled with the
 * `captureAtCast` DSL + `Conditions.CapturedAtCast`; the [ConditionalEffect] picks the 4- or 2-damage
 * branch against the frozen answer.
 */
val SteerClear = card("Steer Clear") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Steer Clear deals 2 damage to target attacking or blocking creature. Steer Clear " +
        "deals 4 damage to that creature instead if you controlled a Mount as you cast this spell."

    spell {
        captureAtCast("controlledMount", Conditions.ControlCreatureOfType(Subtype("Mount")))
        val creature = target("target", TargetCreature(filter = TargetFilter.AttackingOrBlockingCreature))
        effect = ConditionalEffect(
            condition = Conditions.CapturedAtCast("controlledMount"),
            effect = Effects.DealDamage(4, creature),
            elseEffect = Effects.DealDamage(2, creature)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "31"
        artist = "Edgar Sánchez Hidalgo"
        flavorText = "\"Just because you can saddle a critter, doesn't mean it'll let you ride.\"\n—Annie Flash"
        imageUri = "https://cards.scryfall.io/normal/front/5/2/523a4d6e-122b-49b4-bf3d-17d29c0007fb.jpg?1712355352"
    }
}
