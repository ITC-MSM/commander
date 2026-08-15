package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Divine Resilience
 * {W}
 * Instant
 * Kicker {2}{W}
 *
 * Target creature you control gains indestructible until end of turn. If this
 * spell was kicked, instead any number of target creatures you control gain
 * indestructible until end of turn.
 *
 * Modeled like Fight with Fire (DOM): the kicked cast completely swaps the
 * targeting mode (any number of target creatures you control) and the effect
 * (grant indestructible to each chosen target via [ForEachTargetEffect]).
 */
val DivineResilience = card("Divine Resilience") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Kicker {2}{W} (You may pay an additional {2}{W} as you cast this spell.)\n" +
        "Target creature you control gains indestructible until end of turn. If this spell was kicked, " +
        "instead any number of target creatures you control gain indestructible until end of turn. " +
        "(Damage and effects that say \"destroy\" don't destroy them.)"

    keywordAbility(KeywordAbility.kicker("{2}{W}"))

    spell {
        // Unkicked: one target creature you control gains indestructible.
        target = Targets.CreatureYouControl
        effect = Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, EffectTarget.ContextTarget(0))

        // Kicked: any number of target creatures you control gain indestructible.
        kickerTarget = TargetObject(
            unlimited = true,
            filter = TargetFilter.CreatureYouControl,
        )
        kickerEffect = ForEachTargetEffect(
            effects = listOf(
                Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, EffectTarget.ContextTarget(0))
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "10"
        artist = "Svetlin Velinov"
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f3a08245-a535-4d24-b8c0-78759bb9c4b0.jpg?1782689256"
    }
}
