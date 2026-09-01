package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Supreme Verdict
 * {1}{W}{W}{U}
 * Sorcery
 *
 * This spell can't be countered.
 * Destroy all creatures.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * [Effects.DestroyAll] rather than an iteration: it lowers to the gather-then-move pipeline, and
 * the gather reads *projected* state, so a permanent that is a creature only because of a
 * continuous effect is swept too. "Can't be countered" is the intrinsic [cantBeCountered] flag.
 */
val SupremeVerdict = card("Supreme Verdict") {
    manaCost = "{1}{W}{W}{U}"
    colorIdentity = "UW"
    typeLine = "Sorcery"
    oracleText = "This spell can't be countered.\n" +
        "Destroy all creatures."

    cantBeCountered = true

    spell {
        effect = Effects.DestroyAll(GameObjectFilter.Creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "201"
        artist = "Sam Burley"
        flavorText = "Leonos had no second thoughts about the abolishment edict. He'd left skyrunes warning of the eviction, even though it was cloudy."
        imageUri = "https://cards.scryfall.io/normal/front/4/e/4e9648f9-7a67-4717-bca1-861d1f7fed43.jpg?1783940331"
    }
}
