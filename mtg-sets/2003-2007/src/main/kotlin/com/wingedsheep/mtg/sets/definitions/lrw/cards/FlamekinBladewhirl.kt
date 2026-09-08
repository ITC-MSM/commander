package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Flamekin Bladewhirl
 * {R}
 * Creature — Elemental Warrior
 * 2/1
 * As an additional cost to cast this spell, reveal an Elemental card from your hand or pay {3}.
 *
 * An Elemental *card*, so Lorwyn's Kindred noncreature Elementals (Rebellion of the Flamekin,
 * Fire-Belly Changeling's changeling cousins) pay it as well as Elemental creatures.
 */
val FlamekinBladewhirl = card("Flamekin Bladewhirl") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Warrior"
    power = 2
    toughness = 1
    oracleText = "As an additional cost to cast this spell, reveal an Elemental card from your hand " +
        "or pay {3}."

    additionalCost(
        Costs.additional.RevealFromHandOrPay(
            filter = GameObjectFilter.Any.withSubtype(Subtype.ELEMENTAL),
            alternativeManaCost = "{3}"
        )
    )

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "165"
        artist = "Mark Zug"
        imageUri = "https://cards.scryfall.io/normal/front/1/b/1b6ea1c8-ca58-4240-adb7-71cd49664414.jpg?1783942876"
    }
}
