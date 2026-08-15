package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Judgment Bolt
 * {3}{R}
 * Instant
 *
 * Judgment Bolt deals 5 damage to target creature and X damage to that creature's controller,
 * where X is the number of Equipment you control.
 *
 * Two damage clauses share one target: a fixed 5 to the creature, and a dynamic amount equal to
 * the number of Equipment you control ([DynamicAmount.Count]) to its controller, reached via
 * [EffectTarget.TargetController].
 */
val JudgmentBolt = card("Judgment Bolt") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Judgment Bolt deals 5 damage to target creature and X damage to that creature's controller, " +
        "where X is the number of Equipment you control."

    spell {
        val t = target("target creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.DealDamage(5, t),
            Effects.DealDamage(
                DynamicAmount.Count(
                    Player.You,
                    Zone.BATTLEFIELD,
                    GameObjectFilter.Artifact.withSubtype(Subtype.EQUIPMENT),
                ),
                EffectTarget.TargetController,
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "559"
        artist = "Billy Christian"
        flavorText = "\"I'm here to even the odds. Any objections?\""
        imageUri = "https://cards.scryfall.io/normal/front/0/5/05b04b06-9271-4a28-a60e-287df0d1a4d1.jpg?1748707602"
    }
}
