package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Decorum Dissertation
 * {3}{B}{B}
 * Sorcery — Lesson
 *
 * Target player draws two cards and loses 2 life.
 * Paradigm (Then exile this spell. After you first resolve a spell with this name, you may cast a
 * copy of it from exile without paying its mana cost at the beginning of each of your first main
 * phases.)
 *
 * Both effects act on the same chosen player — bind one `target player` and feed it to both
 * [Effects.DrawCards] and [Effects.LoseLife] so the recurring Paradigm copy re-targets each turn.
 * `paradigm()` is the SOS ability word that exiles the spell on resolution and synthesizes the
 * recurring "free copy from exile at your first main phase" ability.
 */
val DecorumDissertation = card("Decorum Dissertation") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery — Lesson"
    oracleText = "Target player draws two cards and loses 2 life.\n" +
        "Paradigm (Then exile this spell. After you first resolve a spell with this name, you may " +
        "cast a copy of it from exile without paying its mana cost at the beginning of each of " +
        "your first main phases.)"

    spell {
        val player = target("target player", Targets.Player)
        effect = Effects.Composite(
            Effects.DrawCards(2, player),
            Effects.LoseLife(2, player),
        )
        paradigm()
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "78"
        artist = "Mila Pesic"
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f4ab2d9b-c73d-478d-aac7-4d3bb24296d2.jpg?1775937454"
    }
}
