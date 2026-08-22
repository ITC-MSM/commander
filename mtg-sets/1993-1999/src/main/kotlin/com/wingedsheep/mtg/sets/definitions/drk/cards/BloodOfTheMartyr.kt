package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RedirectNextDamageEffect
import com.wingedsheep.sdk.scripting.effects.RedirectScope
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Blood of the Martyr
 * {W}{W}{W}
 * Instant
 * Until end of turn, if damage would be dealt to any creature, you may have that damage dealt to
 * you instead.
 *
 * A turn-long, class-wide redirection shield: `RedirectScope.CONTINUOUS` so it is never consumed by
 * use and expires only with the turn, and `creaturesOnly` so it protects *every* creature rather
 * than a fixed list — including creatures that arrive after it resolves, and never a player.
 *
 * **Known divergence, and it is the printed "you may".** The redirect is applied automatically
 * rather than offered per damage instance. Making it optional needs the damage pipeline to stop
 * and ask mid-application, which it cannot currently do for combat damage — the batch is applied
 * as one simultaneous moment (CR 510.2), and there is no decision point inside it. The mandatory
 * form is strictly the aggressive reading: every point of damage that would have hit a creature
 * hits you instead, so a board-wide sweep can kill a caster who would have declined.
 *
 * If a second card ever wants a genuinely optional replacement, the fix is a decision point in the
 * damage pipeline, not a special case here.
 */
val BloodOfTheMartyr = card("Blood of the Martyr") {
    manaCost = "{W}{W}{W}"
    typeLine = "Instant"
    oracleText = "Until end of turn, if damage would be dealt to any creature, you may have that " +
        "damage dealt to you instead."

    spell {
        effect = RedirectNextDamageEffect(
            protectedTargets = emptyList(),
            redirectTo = EffectTarget.Controller,
            scope = RedirectScope.CONTINUOUS,
            creaturesOnly = true,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "2"
        artist = "Christopher Rush"
        imageUri = "https://cards.scryfall.io/normal/front/2/2/22d4761d-acf2-4cb3-86a8-a3f30420a92e.jpg?1783947950"
    }
}
