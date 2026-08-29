package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Witness of Tomorrows
 * {4}{U}
 * Enchantment Creature — Sphinx
 * 3/4
 *
 * Flying
 * {3}{U}: Scry 1.
 *
 * A repeatable mana sink with no tap symbol and no sacrifice, so the activation cost is the bare
 * mana atom ([Costs.Mana]) rather than a composite — the ability can be activated any number of
 * times as long as you can pay.
 */
val WitnessOfTomorrows = card("Witness of Tomorrows") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment Creature — Sphinx"
    power = 3
    toughness = 4
    oracleText = "Flying\n" +
        "{3}{U}: Scry 1."

    keywords(Keyword.FLYING)

    // {3}{U}: Scry 1.
    activatedAbility {
        cost = Costs.Mana("{3}{U}")
        effect = Effects.Scry(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "82"
        artist = "Svetlin Velinov"
        flavorText = "\"As the future slips its way into the present, it ceases to be my concern.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/4/64cd9d60-826c-4b35-9684-dccb0880399e.jpg"
    }
}
