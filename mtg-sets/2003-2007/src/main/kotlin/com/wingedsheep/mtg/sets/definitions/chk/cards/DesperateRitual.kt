package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.splice
import com.wingedsheep.sdk.model.Rarity

/**
 * Desperate Ritual
 * {1}{R}
 * Instant — Arcane
 * Add {R}{R}{R}.
 * Splice onto Arcane {1}{R}
 *
 * A Pyretic Ritual with a type line: the whole spell effect is [Effects.AddMana], no restriction
 * and no rider, so the mana is plain red mana that empties like any other. The splice half is what
 * makes it more than a ritual — grafting "add {R}{R}{R}" onto an Arcane spell refunds most of the
 * splice cost as you cast it.
 */
val DesperateRitual = card("Desperate Ritual") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant — Arcane"
    oracleText = "Add {R}{R}{R}.\n" +
        "Splice onto Arcane {1}{R} (As you cast an Arcane spell, you may reveal this card from " +
        "your hand and pay its splice cost. If you do, add this card's effects to that spell.)"

    splice("{1}{R}")

    spell {
        effect = Effects.AddMana(Color.RED, 3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "163"
        artist = "Darrell Riche"
        imageUri = "https://cards.scryfall.io/normal/front/c/8/c8bdb92a-7bdb-434b-8cc0-873969faf566.jpg?1783944301"
    }
}
