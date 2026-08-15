package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Otter-Penguin
 * {1}{U}
 * Creature — Otter Bird
 * 2/1
 *
 * Whenever you draw your second card each turn, this creature gets +1/+2 until end of
 * turn and can't be blocked this turn.
 */
val OtterPenguin = card("Otter-Penguin") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Otter Bird"
    power = 2
    toughness = 1
    oracleText = "Whenever you draw your second card each turn, this creature gets +1/+2 " +
        "until end of turn and can't be blocked this turn."

    triggeredAbility {
        trigger = Triggers.NthCardDrawn(2)
        effect = Effects.Composite(listOf(
            Effects.ModifyStats(1, 2, EffectTarget.Self),
            Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, EffectTarget.Self)
        ))
        description = "Whenever you draw your second card each turn, this creature gets " +
            "+1/+2 until end of turn and can't be blocked this turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "67"
        artist = "Eilene Cherie"
        flavorText = "\"Will you go penguin sledding with me?\"\n—Aang"
        imageUri = "https://cards.scryfall.io/normal/front/4/8/480b6279-bb34-4b3f-a639-868b52af92b9.jpg?1764120406"
    }
}
