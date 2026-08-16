package com.wingedsheep.mtg.sets.definitions.mh3.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sarpadian Simulacrum — Modern Horizons 3 #135
 * {R} · Artifact Creature — Goblin · 1/1
 *
 * Haste
 * {3}{R}, Sacrifice this creature: It deals 4 damage to target creature.
 *
 * The sacrifice is part of the **activation cost**, not an effect, so it is paid on announcement:
 * [Costs.Composite] of [Costs.Mana] and [Costs.SacrificeSelf] (cf. Mogg Fanatic, Brass Secretary).
 * By the time the ability resolves the Simulacrum is already a graveyard card, which is exactly
 * what "**It** deals 4 damage" describes — the damage source is the permanent as it last existed
 * on the battlefield (CR 608.2h / 112.7a). That is the engine's default source for an ability's
 * damage, so [Effects.DealDamage] is left without an explicit `damageSource`; passing
 * `EffectTarget.Self` would be the same object spelled redundantly.
 */
val SarpadianSimulacrum = card("Sarpadian Simulacrum") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Artifact Creature — Goblin"
    power = 1
    toughness = 1
    oracleText = "Haste\n" +
        "{3}{R}, Sacrifice this creature: It deals 4 damage to target creature."

    keywords(Keyword.HASTE)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{R}"), Costs.SacrificeSelf)
        val t = target("target creature", Targets.Creature)
        effect = Effects.DealDamage(4, t)
        description = "{3}{R}, Sacrifice this creature: It deals 4 damage to target creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "135"
        artist = "Forrest Schehl"
        flavorText = "Built to be super soldiers, the goblins preferred just being super destructive."
        imageUri = "https://cards.scryfall.io/normal/front/0/9/099c65d5-9dd3-41d3-9102-3f6cb30c7b8e.jpg?1783911267"
    }
}
