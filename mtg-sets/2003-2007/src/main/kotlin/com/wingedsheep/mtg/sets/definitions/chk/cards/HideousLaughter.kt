package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.splice
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Hideous Laughter
 * {2}{B}{B}
 * Instant — Arcane
 * All creatures get -2/-2 until end of turn.
 * Splice onto Arcane {3}{B}{B}
 *
 * "All creatures" — not "creatures you control", not "creatures your opponents control" — so the
 * group is [GroupFilter.AllCreatures] and the sweep hits your own board too.
 * `Patterns.Group.modifyStatsForAll` names the group once and applies the modifier per member,
 * which is the single-pass shape the grammar builds.
 */
val HideousLaughter = card("Hideous Laughter") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Instant — Arcane"
    oracleText = "All creatures get -2/-2 until end of turn.\n" +
        "Splice onto Arcane {3}{B}{B} (As you cast an Arcane spell, you may reveal this card from " +
        "your hand and pay its splice cost. If you do, add this card's effects to that spell.)"

    splice("{3}{B}{B}")

    spell {
        effect = Patterns.Group.modifyStatsForAll(-2, -2, GroupFilter.AllCreatures)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "115"
        artist = "Greg Staples"
        imageUri = "https://cards.scryfall.io/normal/front/9/4/941fd135-1c5a-4650-8faf-dfa2c93ec8c9.jpg?1783944315"
    }
}
