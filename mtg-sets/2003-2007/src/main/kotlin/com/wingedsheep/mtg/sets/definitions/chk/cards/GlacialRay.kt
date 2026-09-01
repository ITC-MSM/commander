package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.splice
import com.wingedsheep.sdk.model.Rarity

/**
 * Glacial Ray
 * {1}{R}
 * Instant — Arcane
 * Glacial Ray deals 2 damage to any target.
 * Splice onto Arcane {1}{R}
 *
 * "Any target" is [Targets.Any] — creature, player, or planeswalker. The splice cost equals the
 * mana cost, which is what made this the archetypal splice card: every Arcane spell in the deck
 * becomes a second Glacial Ray.
 */
val GlacialRay = card("Glacial Ray") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant — Arcane"
    oracleText = "Glacial Ray deals 2 damage to any target.\n" +
        "Splice onto Arcane {1}{R} (As you cast an Arcane spell, you may reveal this card from " +
        "your hand and pay its splice cost. If you do, add this card's effects to that spell.)"

    splice("{1}{R}")

    spell {
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(2, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "168"
        artist = "Jim Murray"
        imageUri = "https://cards.scryfall.io/normal/front/5/6/5637a3b0-6204-4893-878e-c34babeab2e6.jpg?1783944301"
    }
}
