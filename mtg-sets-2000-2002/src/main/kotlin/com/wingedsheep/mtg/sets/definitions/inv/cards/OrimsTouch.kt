package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Orim's Touch
 * {W}
 * Instant
 * Kicker {1}
 *
 * Prevent the next 2 damage that would be dealt to any target this turn. If this
 * spell was kicked, prevent the next 4 damage that would be dealt to that permanent
 * or player this turn instead.
 */
val OrimsTouch = card("Orim's Touch") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Kicker {1} (You may pay an additional {1} as you cast this spell.)\n" +
        "Prevent the next 2 damage that would be dealt to any target this turn. " +
        "If this spell was kicked, prevent the next 4 damage that would be dealt to " +
        "that permanent or player this turn instead."

    keywordAbility(KeywordAbility.kicker("{1}"))

    spell {
        target = Targets.Any
        effect = ConditionalEffect(
            condition = WasKicked,
            effect = Effects.PreventNextDamage(4, EffectTarget.ContextTarget(0)),
            elseEffect = Effects.PreventNextDamage(2, EffectTarget.ContextTarget(0))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "23"
        artist = "Roger Raupp"
        imageUri = "https://cards.scryfall.io/normal/front/5/5/559f551e-7891-4c6d-8798-a25c0255fa3b.jpg?1562912440"
    }
}
