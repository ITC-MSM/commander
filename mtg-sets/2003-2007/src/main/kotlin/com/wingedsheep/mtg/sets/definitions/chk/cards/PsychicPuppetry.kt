package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.splice
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ModalEffect

/**
 * Psychic Puppetry
 * {1}{U}
 * Instant — Arcane
 * You may tap or untap target permanent.
 * Splice onto Arcane {U}
 *
 * The Pestermite idiom: a [MayEffect] (the printed "you may") wrapped around a two-[Mode]
 * [ModalEffect] over the one declared target, with `countsAsModalSpell = false` so the tap/untap
 * choice isn't read as a modal *spell* — it's a choice made on resolution, after the target is
 * locked in. That ordering matters: if an opponent taps the permanent in response, you simply pick
 * the untap half instead.
 */
val PsychicPuppetry = card("Psychic Puppetry") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant — Arcane"
    oracleText = "You may tap or untap target permanent.\n" +
        "Splice onto Arcane {U} (As you cast an Arcane spell, you may reveal this card from your " +
        "hand and pay its splice cost. If you do, add this card's effects to that spell.)"

    splice("{U}")

    spell {
        val permanent = target("target", Targets.Permanent)
        effect = MayEffect(
            ModalEffect(
                modes = listOf(
                    Mode.noTarget(Effects.Tap(permanent)),
                    Mode.noTarget(Effects.Untap(permanent))
                ),
                countsAsModalSpell = false
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "80"
        artist = "Joel Thomas"
        imageUri = "https://cards.scryfall.io/normal/front/9/e/9e341d6b-f4b0-4347-8055-f5fab756334c.jpg?1783944323"
    }
}
