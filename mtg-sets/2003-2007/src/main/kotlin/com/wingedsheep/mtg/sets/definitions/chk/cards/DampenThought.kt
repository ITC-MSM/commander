package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.splice
import com.wingedsheep.sdk.model.Rarity

/**
 * Dampen Thought
 * {1}{U}
 * Instant — Arcane
 * Target player mills four cards.
 * Splice onto Arcane {1}{U}
 *
 * `Patterns.Library.mill` is the Gather-then-Move pipeline the mill sentence compiles to: the
 * four cards are gathered off the top of the *target* player's library (`isMill = true`) and then
 * moved to that same player's graveyard as one batch, so a single zone-change batch trigger sees
 * all four.
 */
val DampenThought = card("Dampen Thought") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant — Arcane"
    oracleText = "Target player mills four cards.\n" +
        "Splice onto Arcane {1}{U} (As you cast an Arcane spell, you may reveal this card from " +
        "your hand and pay its splice cost. If you do, add this card's effects to that spell.)"

    splice("{1}{U}")

    spell {
        val player = target("target", Targets.Player)
        effect = Patterns.Library.mill(4, player)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "57"
        artist = "Arnie Swekel"
        imageUri = "https://cards.scryfall.io/normal/front/a/4/a4806218-ff27-4df3-922d-5a085ffa44a6.jpg?1783944329"
    }
}
