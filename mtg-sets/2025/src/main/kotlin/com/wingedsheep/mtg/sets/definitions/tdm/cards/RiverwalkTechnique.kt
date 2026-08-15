package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Riverwalk Technique
 * {3}{U}
 * Instant
 *
 * Choose one —
 * • The owner of target nonland permanent puts it on their choice of the top or bottom of their library.
 * • Counter target noncreature spell.
 */
val RiverwalkTechnique = card("Riverwalk Technique") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• The owner of target nonland permanent puts it on their choice of the top or bottom of their library.\n" +
        "• Counter target noncreature spell."

    spell {
        effect = ModalEffect.chooseOne(
            // Owner of target nonland permanent puts it on top or bottom of their library.
            Mode(
                effect = Effects.PutOnTopOrBottomOfLibrary(EffectTarget.ContextTarget(0)),
                targetRequirements = listOf(Targets.NonlandPermanent),
                description = "The owner of target nonland permanent puts it on their choice of the top or bottom of their library"
            ),
            // Counter target noncreature spell.
            Mode(
                effect = Effects.CounterSpell(),
                targetRequirements = listOf(Targets.NoncreatureSpell),
                description = "Counter target noncreature spell"
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "54"
        artist = "Julia Metzger"
        imageUri = "https://cards.scryfall.io/normal/front/0/4/043c25d5-13ee-4cab-98d5-fb89db9cf6e3.jpg?1743204178"
    }
}
