package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBlock

/**
 * Reckless Imp
 * {2}{B}
 * Creature — Imp
 * 2 / 2
 *
 * Flying
 * This creature can't block.
 * Dash {1}{B} (You may cast this spell for its dash cost. If you do, it gains haste, and it's returned from the battlefield to its owner's hand at the beginning of the next end step.)
 *
 * "This creature can't block" is the source-scoped [CantBlock] static ability — its default
 * `GroupFilter.source()` is exactly the printed subject, so no filter is spelled. `dash` is a
 * builder property rather than a [Keyword] constant, and setting it is what adds the
 * `KeywordAbility.Dash` the cast enumerator reads; the blocking restriction costs a dashed Imp
 * nothing, since it leaves before the opponent's combat anyway.
 */
val RecklessImp = card("Reckless Imp") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Imp"
    power = 2
    toughness = 2
    oracleText = "Flying\n" +
        "This creature can't block.\n" +
        "Dash {1}{B} (You may cast this spell for its dash cost. If you do, it gains haste, and it's returned from the battlefield to its owner's hand at the beginning of the next end step.)"

    keywords(Keyword.FLYING)

    staticAbility {
        ability = CantBlock()
    }

    dash = "{1}{B}"

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "115"
        artist = "Torstein Nordstrand"
        imageUri = "https://cards.scryfall.io/normal/front/7/4/74a44e0a-5fff-4fc0-a76d-1b097f1d4d5d.jpg?1783938594"
    }
}
