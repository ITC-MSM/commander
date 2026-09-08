package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Squeaking Pie Sneak
 * {1}{B}
 * Creature — Goblin Rogue
 * 2/2
 * As an additional cost to cast this spell, reveal a Goblin card from your hand or pay {3}.
 * Fear
 *
 * A Goblin *card* pays it, so Lorwyn's Kindred noncreature Goblins (Tarfire) count.
 */
val SqueakingPieSneak = card("Squeaking Pie Sneak") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Goblin Rogue"
    power = 2
    toughness = 2
    oracleText = "As an additional cost to cast this spell, reveal a Goblin card from your hand or " +
        "pay {3}.\nFear (This creature can't be blocked except by artifact creatures and/or black " +
        "creatures.)"

    additionalCost(
        Costs.additional.RevealFromHandOrPay(
            filter = GameObjectFilter.Any.withSubtype(Subtype.GOBLIN),
            alternativeManaCost = "{3}"
        )
    )

    keywords(Keyword.FEAR)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "142"
        artist = "Jeff Miracola"
        imageUri = "https://cards.scryfall.io/normal/front/b/7/b7184715-3ed3-4f56-9bf3-ff431cca86be.jpg?1783942883"
    }
}
