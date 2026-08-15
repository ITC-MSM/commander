package com.wingedsheep.mtg.sets.definitions.ogw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantCantBeCountered

/**
 * Sphinx of the Final Word
 * {5}{U}{U}
 * Creature — Sphinx
 * 5/5
 * This spell can't be countered.
 * Flying
 * Hexproof
 * Instant and sorcery spells you control can't be countered.
 *
 * "This spell can't be countered" is the card-level [cantBeCountered] flag (it applies while the
 * Sphinx itself is on the stack, before any permanent exists). The last line is a battlefield
 * static ability; the filter is scoped with `youControl()` so it protects only its controller's
 * spells — [GrantCantBeCountered] evaluates the filter with the granter's controller as the
 * predicate context.
 */
val SphinxOfTheFinalWord = card("Sphinx of the Final Word") {
    manaCost = "{5}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Sphinx"
    power = 5
    toughness = 5
    oracleText = "This spell can't be countered.\n" +
        "Flying\n" +
        "Hexproof (This creature can't be the target of spells or abilities your opponents control.)\n" +
        "Instant and sorcery spells you control can't be countered."

    keywords(Keyword.FLYING, Keyword.HEXPROOF)

    cantBeCountered = true

    staticAbility {
        ability = GrantCantBeCountered(
            filter = GameObjectFilter.InstantOrSorcery.youControl()
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "63"
        artist = "Lius Lasahido"
        flavorText = "He answers questions as readily as he asks them, but his answer is always \"no.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/6/866f92a3-2738-4e0f-adda-3ff9227dc17a.jpg?1783937917"
    }
}
