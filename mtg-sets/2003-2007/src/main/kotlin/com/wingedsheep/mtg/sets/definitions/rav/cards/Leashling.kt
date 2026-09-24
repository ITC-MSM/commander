package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Leashling
 * {6}
 * Artifact Creature — Dog
 * 3/3
 * Put a card from your hand on top of your library: Return this creature to its owner's hand.
 *
 * The cost is [Costs.PutFromHandOnTopOfLibrary] — a real cost, paid while activating (so the card
 * is back on the library before anyone can respond), and not a discard, so nothing reaches the
 * graveyard. With an empty hand the ability can't be activated. There is no {T} in the cost, so
 * summoning sickness doesn't matter.
 */
val Leashling = card("Leashling") {
    manaCost = "{6}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Dog"
    oracleText = "Put a card from your hand on top of your library: Return this creature to its owner's hand."
    power = 3
    toughness = 3

    activatedAbility {
        cost = Costs.PutFromHandOnTopOfLibrary()
        effect = Effects.ReturnToHand(EffectTarget.Self)
        description = "Put a card from your hand on top of your library: Return this creature to its owner's hand."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "265"
        artist = "Carl Critchlow"
        flavorText = "Constructed of leather and irony."
        imageUri = "https://cards.scryfall.io/normal/front/1/3/132551d3-ef8e-4ed0-a363-53db4f0621f0.jpg?1783943596"
    }
}
