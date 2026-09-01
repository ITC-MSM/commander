package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.splice
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Lifted by Clouds
 * {2}{U}
 * Instant — Arcane
 * Target creature gains flying until end of turn.
 * Splice onto Arcane {1}{U}
 *
 * `Effects.GrantKeyword` defaults to `Duration.EndOfTurn`, which is exactly the printed duration.
 * The splice cost ({1}{U}) is a mana cheaper than casting the card outright.
 */
val LiftedByClouds = card("Lifted by Clouds") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Instant — Arcane"
    oracleText = "Target creature gains flying until end of turn.\n" +
        "Splice onto Arcane {1}{U} (As you cast an Arcane spell, you may reveal this card from " +
        "your hand and pay its splice cost. If you do, add this card's effects to that spell.)"

    splice("{1}{U}")

    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.GrantKeyword(Keyword.FLYING, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "73"
        artist = "Darrell Riche"
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a5f08f3d-82ef-4c3f-af2d-a3c834e22b99.jpg?1783944325"
    }
}
