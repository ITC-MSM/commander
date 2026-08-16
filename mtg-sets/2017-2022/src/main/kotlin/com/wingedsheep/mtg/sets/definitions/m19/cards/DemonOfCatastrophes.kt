package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Demon of Catastrophes
 * {2}{B}{B}
 * Creature — Demon
 * 6/6
 * As an additional cost to cast this spell, sacrifice a creature.
 * Flying, trample
 */
val DemonOfCatastrophes = card("Demon of Catastrophes") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Demon"
    power = 6
    toughness = 6
    oracleText = "As an additional cost to cast this spell, sacrifice a creature.\nFlying, trample"

    additionalCost(Costs.additional.SacrificePermanent(GameObjectFilter.Creature))

    keywords(Keyword.FLYING, Keyword.TRAMPLE)

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "91"
        artist = "Sidharth Chaturvedi"
        flavorText = "A pit yawned open, a column of oily smoke arose, and a towering form hissed, \"I accept this offering.\""
        imageUri = "https://cards.scryfall.io/normal/front/d/5/d50f9563-7bf8-4c3c-ac82-327221e56551.jpg?1783934573"
        ruling(
            "2018-07-13",
            "You must sacrifice exactly one creature to cast Demon of Catastrophes; you can't cast " +
                "it without sacrificing a creature, and you can't sacrifice additional creatures."
        )
        ruling(
            "2018-07-13",
            "Players can respond only after Demon of Catastrophes has been cast and all its costs " +
                "have been paid. No one can try to destroy the creature you sacrificed to prevent " +
                "you from casting this spell."
        )
    }
}
