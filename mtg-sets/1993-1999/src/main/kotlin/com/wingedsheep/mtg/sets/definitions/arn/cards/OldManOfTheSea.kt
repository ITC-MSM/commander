package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.GainControlEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Old Man of the Sea
 * {1}{U}{U}
 * Creature — Djinn
 * 2/3
 * You may choose not to untap this creature during your untap step.
 * {T}: Gain control of target creature with power less than or equal to this creature's
 * power for as long as this creature remains tapped and that creature's power remains
 * less than or equal to this creature's power.
 *
 * Both halves of the duration are gated by [com.wingedsheep.engine.mechanics.layers.StateProjector]
 * for the instantaneous view: the projector skips the floating control effect when the source
 * isn't tapped, and reverts the target's controller in a post-Layer-7 fix-up the moment its
 * projected power exceeds the Old Man's projected power (counters, +X/+Y floating effects,
 * lord-style anthems). Per CR 611.2b the duration is one-way: once either half fails,
 * [com.wingedsheep.engine.mechanics.sba.permanent.EndedDurationExpiryCheck] physically removes
 * the floating effect, so a pump that later wears off — or a re-tap — does not re-steal the
 * creature.
 */
val OldManOfTheSea = card("Old Man of the Sea") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Djinn"
    power = 2
    toughness = 3
    oracleText = "You may choose not to untap this creature during your untap step.\n" +
        "{T}: Gain control of target creature with power less than or equal to this creature's " +
        "power for as long as this creature remains tapped and that creature's power remains " +
        "less than or equal to this creature's power."

    flags(AbilityFlag.MAY_NOT_UNTAP)

    activatedAbility {
        cost = Costs.Tap
        val t = target("target", TargetCreature(
            filter = TargetFilter(GameObjectFilter.Creature.powerAtMostEntity(EntityReference.Source))
        ))
        effect = GainControlEffect(
            t,
            Duration.WhileSourceTappedAndAffectedPowerAtMostSource("Old Man of the Sea")
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "18"
        artist = "Susan Van Camp"
        imageUri = "https://cards.scryfall.io/normal/front/d/1/d10f8a05-78b0-42a7-adcd-83f6bafe5417.jpg?1562934067"
    }
}
