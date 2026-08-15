package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * General Traag, Heart of Stone
 * {3}{R}{R}
 * Legendary Artifact Creature — Elemental Soldier
 * 4/3
 *
 * Trample
 * When General Traag enters, you may sacrifice another artifact. When you do,
 * General Traag deals 4 damage to target creature.
 */
val GeneralTraagHeartOfStone = card("General Traag, Heart of Stone") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Artifact Creature — Elemental Soldier"
    oracleText = "Trample\nWhen General Traag enters, you may sacrifice another artifact. When you do, General Traag deals 4 damage to target creature."
    power = 4
    toughness = 3

    keywords(Keyword.TRAMPLE)

    // "you may sacrifice another artifact. When you do, [deal 4 to target creature]" —
    // the reflexive trigger picks its target only after the optional sacrifice is paid.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ReflexiveTriggerEffect(
            action = SacrificeEffect(GameObjectFilter.Artifact, excludeSource = true),
            optional = true,
            reflexiveEffect = Effects.DealDamage(4, EffectTarget.ContextTarget(0), damageSource = EffectTarget.Self),
            reflexiveTargetRequirements = listOf(Targets.Creature)
        )
        description = "When General Traag enters, you may sacrifice another artifact. When you do, General Traag deals 4 damage to target creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "90"
        artist = "Kevin Sidharta"
        flavorText = "\"Lord Krang, we arrive. We obey. We conquer.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/0/e02e971f-6008-4c82-acd2-2aed13009ccb.jpg?1771502622"
    }
}
