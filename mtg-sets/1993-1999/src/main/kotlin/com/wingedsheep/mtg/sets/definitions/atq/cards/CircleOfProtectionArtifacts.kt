package com.wingedsheep.mtg.sets.definitions.atq.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Circle of Protection: Artifacts
 * {1}{W}
 * Enchantment
 * {2}: The next time an artifact source of your choice would deal damage to you this turn, prevent
 *   that damage.
 *
 * Modeling note: the Circle of Protection family — a single-instance prevention shield keyed to a
 * chosen source, here constrained to artifact sources
 * ([Effects.PreventNextDamageFromChosenArtifactSource]). Only artifact permanents/spells are offered
 * for the choice; the next whole damage instance from the chosen source is prevented, then the
 * shield is consumed.
 */
val CircleOfProtectionArtifacts = card("Circle of Protection: Artifacts") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "{2}: The next time an artifact source of your choice would deal damage to you this " +
        "turn, prevent that damage."

    activatedAbility {
        cost = Costs.Mana("{2}")
        effect = Effects.PreventNextDamageFromChosenArtifactSource()
        description = "{2}: The next time an artifact source of your choice would deal damage to you this turn, prevent that damage."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "4"
        artist = "Pete Venters"
        imageUri = "https://cards.scryfall.io/normal/front/2/2/22ebd5a3-fef8-4097-b038-89a6cb38227d.jpg?1562902368"
    }
}
