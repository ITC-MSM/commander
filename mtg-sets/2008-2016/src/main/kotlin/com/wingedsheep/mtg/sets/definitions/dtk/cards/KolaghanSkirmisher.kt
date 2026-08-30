package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kolaghan Skirmisher
 * {1}{B}
 * Creature — Human Warrior
 * 2 / 2
 *
 * Dash {2}{B} (You may cast this spell for its dash cost. If you do, it gains haste, and it's returned from the battlefield to its owner's hand at the beginning of the next end step.)
 *
 * A vanilla 2/2 whose whole body is the dash cost. `dash` is a builder property rather than a
 * `Keyword` constant, and setting it is what adds the `KeywordAbility.Dash` the cast enumerator
 * reads — the alternative cost, the haste and the next-end-step bounce all come from that one line.
 */
val KolaghanSkirmisher = card("Kolaghan Skirmisher") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Warrior"
    power = 2
    toughness = 2
    oracleText = "Dash {2}{B} (You may cast this spell for its dash cost. If you do, it gains haste, and it's returned from the battlefield to its owner's hand at the beginning of the next end step.)"

    dash = "{2}{B}"

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "107"
        artist = "Anthony Palumbo"
        flavorText = "Kolaghan's army rushes from kill to kill, desperate to avoid the dragon's wrath."
        imageUri = "https://cards.scryfall.io/normal/front/c/e/ced0adca-d11c-41b6-aec4-4799946425d3.jpg?1783938597"
    }
}
