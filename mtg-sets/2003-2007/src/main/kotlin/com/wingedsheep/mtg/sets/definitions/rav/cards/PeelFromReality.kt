package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Peel from Reality
 * {1}{U}
 * Instant
 *
 * Return target creature you control and target creature you don't control to their owners' hands.
 *
 * Two separate single-target requirements rather than one count-2 requirement: the two
 * slots carry different filters, and each is checked for legality independently on
 * resolution, so one illegal target still lets the other bounce (CR 608.2b).
 */
val PeelFromReality = card("Peel from Reality") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Return target creature you control and target creature you don't control to their owners' hands."

    spell {
        val yours = target("target creature you control", Targets.CreatureYouControl)
        val theirs = target("target creature you don't control", Targets.CreatureOpponentControls)
        effect = Effects.ReturnToHand(yours) then Effects.ReturnToHand(theirs)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "61"
        artist = "Puddnhead"
        flavorText = "When House Dimir's secrets are in danger of exposure, the guild takes drastic measures to cover its tracks."
        imageUri = "https://cards.scryfall.io/normal/front/e/4/e4e6ca71-ba17-4a16-a331-b787363874e2.jpg?1783943682"
    }
}
