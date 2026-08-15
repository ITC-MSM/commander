package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect

/**
 * Vayne's Treachery
 * {1}{B}
 * Instant
 * Kicker—Sacrifice an artifact or creature.
 * Target creature gets -2/-2 until end of turn. If this spell was kicked,
 * that creature gets -6/-6 until end of turn instead.
 *
 * Same shape as Vicious Offering: the kicked branch replaces (-2/-2 → -6/-6) rather than
 * stacking, so it is modeled as a [ConditionalEffect] on [WasKicked] with an else branch.
 */
val VaynesTreachery = card("Vayne's Treachery") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Kicker—Sacrifice an artifact or creature. (You may sacrifice an artifact or creature in addition to any other costs as you cast this spell.)\n" +
        "Target creature gets -2/-2 until end of turn. If this spell was kicked, that creature gets -6/-6 until end of turn instead."

    keywordAbility(KeywordAbility.kicker(Costs.additional.SacrificePermanent(filter = GameObjectFilter.CreatureOrArtifact)))

    spell {
        val t = target("target", Targets.Creature)
        effect = ConditionalEffect(
            condition = WasKicked,
            effect = ModifyStatsEffect(-6, -6, t, Duration.EndOfTurn),
            elseEffect = ModifyStatsEffect(-2, -2, t, Duration.EndOfTurn)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "124"
        artist = "Touge369"
        imageUri = "https://cards.scryfall.io/normal/front/6/d/6de6d23b-7d42-41c1-be1c-010fe43ee586.jpg?1748706226"
    }
}
