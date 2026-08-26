package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Wanderer's Intervention — Kamigawa: Neon Dynasty #41 (canonical printing)
 * {1}{W} · Instant
 *
 * Wanderer's Intervention deals 4 damage to target attacking or blocking creature.
 *
 * White removal gated on combat — the target restriction is
 * [TargetFilter.AttackingOrBlockingCreature], a *state* predicate, so a creature that leaves combat
 * in response makes the spell fizzle.
 */
val WanderersIntervention = card("Wanderer's Intervention") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Wanderer's Intervention deals 4 damage to target attacking or blocking creature."

    spell {
        val t = target(
            "attacking or blocking creature",
            TargetCreature(filter = TargetFilter.AttackingOrBlockingCreature),
        )
        effect = DealDamageEffect(4, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "41"
        artist = "Cristi Balanescu"
        flavorText = "Just as Jin-Gitaxias's victory seemed certain, the Wanderer appeared, her " +
            "blade already in motion. One mighty slash was enough to take the praetor out of the fight."
        imageUri = "https://cards.scryfall.io/normal/front/4/3/43708ec9-a85a-4244-86b4-67b30b41d854.jpg?1783923911"
    }
}
