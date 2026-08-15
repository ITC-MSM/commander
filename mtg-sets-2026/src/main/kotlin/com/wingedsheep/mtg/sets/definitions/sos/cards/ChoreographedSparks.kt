package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Choreographed Sparks
 * {R}{R}
 * Instant
 * This spell can't be copied.
 * Choose one or both —
 * • Copy target instant or sorcery spell you control. You may choose new targets for the copy.
 * • Copy target creature spell you control. The copy gains haste and
 *   "At the beginning of the end step, sacrifice this token."
 *
 * Mode 1 is the plain [Effects.CopyTargetSpell] retarget path. Mode 2 copies a creature spell,
 * which becomes a token as it resolves (CR 707.10f); the new `addedTokenKeywords` / `sacrificeTokenAtStep`
 * riders on `CopyTargetSpell` grant the resulting token haste and register the delayed "sacrifice
 * this token" trigger at the next end step. `cantBeCopied` maps the "This spell can't be copied" line.
 */
val ChoreographedSparks = card("Choreographed Sparks") {
    manaCost = "{R}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "This spell can't be copied.\n" +
        "Choose one or both —\n" +
        "• Copy target instant or sorcery spell you control. You may choose new targets for the copy.\n" +
        "• Copy target creature spell you control. The copy gains haste and " +
        "\"At the beginning of the end step, sacrifice this token.\""

    cantBeCopied = true

    spell {
        modal(chooseCount = 2, minChooseCount = 1) {
            mode("Copy target instant or sorcery spell you control. You may choose new targets for the copy") {
                val spell = target("target instant or sorcery spell you control", Targets.InstantOrSorcerySpellYouControl)
                effect = Effects.CopyTargetSpell(target = spell)
            }
            mode("Copy target creature spell you control. The copy gains haste and \"At the beginning of the end step, sacrifice this token.\"") {
                val spell = target("target creature spell you control", Targets.CreatureSpellYouControl)
                effect = Effects.CopyTargetSpell(
                    target = spell,
                    addedTokenKeywords = setOf(Keyword.HASTE),
                    sacrificeTokenAtStep = Step.END
                )
            }
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "111"
        artist = "Paolo Parente"
        imageUri = "https://cards.scryfall.io/normal/front/0/c/0cda4235-4dce-48fe-a8a5-2a952dedbe25.jpg?1776583993"
    }
}
