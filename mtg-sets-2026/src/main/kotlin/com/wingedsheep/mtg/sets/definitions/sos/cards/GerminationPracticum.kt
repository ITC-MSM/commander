package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Germination Practicum
 * {3}{G}{G}
 * Sorcery — Lesson
 *
 * Put two +1/+1 counters on each creature you control.
 * Paradigm (Then exile this spell. After you first resolve a spell with this name, you may cast a
 * copy of it from exile without paying its mana cost at the beginning of each of your first main
 * phases.)
 *
 * Snapshots "each creature you control" with [Effects.ForEachInGroup] over
 * [GroupFilter.AllCreaturesYouControl] and puts two +1/+1 counters on each iterated creature
 * ([EffectTarget.Self] rebinds to the current creature). `paradigm()` exiles the spell on resolve
 * and synthesizes the recurring free copy.
 */
val GerminationPracticum = card("Germination Practicum") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery — Lesson"
    oracleText = "Put two +1/+1 counters on each creature you control.\n" +
        "Paradigm (Then exile this spell. After you first resolve a spell with this name, you may " +
        "cast a copy of it from exile without paying its mana cost at the beginning of each of " +
        "your first main phases.)"

    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter.AllCreaturesYouControl,
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self),
        )
        paradigm()
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "149"
        artist = "Johan Grenier"
        imageUri = "https://cards.scryfall.io/normal/front/a/b/abe8332f-c76e-44e2-9427-d1228453abec.jpg?1775938016"
    }
}
