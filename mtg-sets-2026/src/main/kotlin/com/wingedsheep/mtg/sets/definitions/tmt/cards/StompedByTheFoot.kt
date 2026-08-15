package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect
import com.wingedsheep.sdk.scripting.predicates.CardPredicate

/**
 * Stomped by the Foot
 * {1}{B}
 * Instant
 *
 * Kicker—Sacrifice an artifact or creature.
 * Target creature gets -2/-2 until end of turn. If this spell was
 * kicked, that creature gets -5/-5 until end of turn instead.
 */
val StompedByTheFoot = card("Stomped by the Foot") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Kicker—Sacrifice an artifact or creature. (You may sacrifice an artifact or creature in addition to any other costs as you cast this spell.)\nTarget creature gets -2/-2 until end of turn. If this spell was kicked, that creature gets -5/-5 until end of turn instead."

    keywordAbility(
        KeywordAbility.kicker(
            Costs.additional.SacrificePermanent(
                filter = GameObjectFilter(
                    cardPredicates = listOf(
                        CardPredicate.Or(
                            listOf(
                                CardPredicate.IsArtifact,
                                CardPredicate.IsCreature,
                            )
                        )
                    )
                )
            )
        )
    )

    spell {
        val t = target("target creature", Targets.Creature)
        effect = ConditionalEffect(
            condition = WasKicked,
            effect = ModifyStatsEffect(-5, -5, t, Duration.EndOfTurn),
            elseEffect = ModifyStatsEffect(-2, -2, t, Duration.EndOfTurn)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "82"
        artist = "Kim Sokol"
        flavorText = "\"These guys are good!\"\n—Leonardo"
        imageUri = "https://cards.scryfall.io/normal/front/3/f/3fdec16f-77f5-4792-9698-b9099a204028.jpg?1771586922"
    }
}
