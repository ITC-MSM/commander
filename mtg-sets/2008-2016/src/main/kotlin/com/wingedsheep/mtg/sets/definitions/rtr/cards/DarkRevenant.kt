package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dark Revenant
 * {3}{B}
 * Creature — Spirit
 * 2/2
 *
 * Flying
 * When this creature dies, put it on top of its owner's library.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * A dies trigger that moves the card on from the graveyard it has already reached — the engine
 * resolves [EffectTarget.Self] against the card's new object, so no last-known-information
 * plumbing is needed for the move itself.
 */
val DarkRevenant = card("Dark Revenant") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Spirit"
    oracleText = "Flying\n" +
        "When this creature dies, put it on top of its owner's library."
    power = 2
    toughness = 2

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.PutOnTopOfLibrary(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "61"
        artist = "Daarken"
        flavorText = "The murderer faced justice in the Azorius courts, but his soul was set free on a technicality."
        imageUri = "https://cards.scryfall.io/normal/front/2/1/2167cc6d-ddb5-4f13-8905-a0c5123b852a.jpg?1783940364"
    }
}
