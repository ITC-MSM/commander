package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Darkness
 * {B}
 * Instant
 *
 * Prevent all combat damage that would be dealt this turn.
 */
val Darkness = card("Darkness") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Prevent all combat damage that would be dealt this turn."

    spell {
        effect = Effects.PreventAllCombatDamage()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "94"
        artist = "Harold McNeill"
        flavorText = "\"If I must die, I will encounter darkness as a bride,/ And hug it in my arms.\" —William " +
            "Shakespeare, *Measure for Measure*"
        imageUri = "https://cards.scryfall.io/normal/front/5/3/53b04dab-45b7-418b-a0f0-bcf35145fc53.jpg?1783948067"
    }
}
