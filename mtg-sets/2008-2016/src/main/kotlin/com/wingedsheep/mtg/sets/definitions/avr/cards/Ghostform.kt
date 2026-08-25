package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Ghostform
 * {1}{U}
 * Sorcery
 * Up to two target creatures can't be blocked this turn.
 *
 * "Can't be blocked" is an evasion [AbilityFlag] rather than a [com.wingedsheep.sdk.core.Keyword] —
 * there is no keyword for it to abbreviate — and the blocking rules read the flag directly when
 * legal blocks are enumerated. "Up to two" is one target requirement with `count = 2, optional = true`,
 * and [ForEachTargetEffect] runs the grant once per target that was actually chosen.
 */
val Ghostform = card("Ghostform") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Up to two target creatures can't be blocked this turn."

    spell {
        target = TargetCreature(count = 2, optional = true)
        effect = ForEachTargetEffect(
            effects = listOf(
                Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, EffectTarget.ContextTarget(0))
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "56"
        artist = "Scott Chou"
        flavorText = "\"There's no such thing as 'impenetrable.'\""
        imageUri = "https://cards.scryfall.io/normal/front/1/f/1f6a20ba-6691-4844-9685-dfcd4184224e.jpg"
    }
}
