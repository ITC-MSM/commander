package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Screamreach Brawler
 * {2}{R}
 * Creature — Orc Berserker
 * 2 / 3
 *
 * Dash {1}{R} (You may cast this spell for its dash cost. If you do, it gains haste, and it's returned from the battlefield to its owner's hand at the beginning of the next end step.)
 *
 * Kolaghan Skirmisher's shape in red: the printed body is the dash cost alone. `dash` is a builder
 * property rather than a `Keyword` constant, and setting it is what adds the `KeywordAbility.Dash`
 * the cast enumerator reads — here the dash cost is cheaper than the mana cost, so the Orc is
 * usually a one-turn 2/3 haste for {1}{R}.
 */
val ScreamreachBrawler = card("Screamreach Brawler") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Orc Berserker"
    power = 2
    toughness = 3
    oracleText = "Dash {1}{R} (You may cast this spell for its dash cost. If you do, it gains haste, and it's returned from the battlefield to its owner's hand at the beginning of the next end step.)"

    dash = "{1}{R}"

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "155"
        artist = "Slawomir Maniak"
        flavorText = "\"My dragonlord's lightning will dance upon your bones!\""
        imageUri = "https://cards.scryfall.io/normal/front/6/b/6bfab1c5-e267-43a9-ae16-f6b8ae531eef.jpg?1783938586"
    }
}
