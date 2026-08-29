package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vedalken Entrancer
 * {3}{U}
 * Creature — Vedalken Wizard
 * 1/4
 * {U}, {T}: Target player mills two cards.
 */
val VedalkenEntrancer = card("Vedalken Entrancer") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Vedalken Wizard"
    oracleText = "{U}, {T}: Target player mills two cards."
    power = 1
    toughness = 4

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{U}"), Costs.Tap)
        val p = target("target player", Targets.Player)
        effect = Patterns.Library.mill(2, p)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "74"
        artist = "Dan Murayama Scott"
        flavorText = "Their denial reaches far into your future."
        imageUri = "https://cards.scryfall.io/normal/front/f/a/faf5e4b8-3bb9-4a4c-b8fa-2cae5372ba24.jpg"
    }
}
