package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.PayOrSufferEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeEffect

/**
 * Bebop & Rocksteady
 * {1}{B/G}{B/G}
 * Legendary Creature — Boar Rhino Mutant
 * 7/5
 *
 * Whenever Bebop & Rocksteady attack or block, sacrifice a permanent unless you
 * discard a card.
 */
val BebopAndRocksteady = card("Bebop & Rocksteady") {
    manaCost = "{1}{B/G}{B/G}"
    colorIdentity = "BG"
    typeLine = "Legendary Creature — Boar Rhino Mutant"
    oracleText = "Whenever Bebop & Rocksteady attack or block, sacrifice a permanent unless you discard a card."
    power = 7
    toughness = 5

    // "sacrifice a permanent unless you discard a card" — pay (discard) to avoid the
    // suffer (sacrifice a permanent you control), Masticore's PayOrSuffer idiom.
    val sacrificeUnlessDiscard = PayOrSufferEffect(
        cost = Costs.pay.Discard(),
        suffer = SacrificeEffect(GameObjectFilter.Any)
    )

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = sacrificeUnlessDiscard
        description = "Whenever Bebop & Rocksteady attack, sacrifice a permanent unless you discard a card."
    }

    triggeredAbility {
        trigger = Triggers.Blocks
        effect = sacrificeUnlessDiscard
        description = "Whenever Bebop & Rocksteady block, sacrifice a permanent unless you discard a card."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "140"
        artist = "Néstor Ossandón Leal"
        flavorText = "\"They'll beat you with fists, with a bat, with a knife. / So don't try to stop them, just run for your life! / Get set and get ready, / For Bebop and Rocksteady!\""
        imageUri = "https://cards.scryfall.io/normal/front/5/3/535461bd-f763-408b-816f-64b7bfb9210d.jpg?1760102796"
    }
}
