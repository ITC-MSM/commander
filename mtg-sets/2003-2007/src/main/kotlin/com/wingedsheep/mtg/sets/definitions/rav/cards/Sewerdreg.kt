package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sewerdreg
 * {3}{B}{B}
 * Creature — Spirit
 * 3/3
 * Swampwalk (This creature can't be blocked as long as defending player controls a Swamp.)
 * Sacrifice this creature: Exile target card from a graveyard.
 *
 * "A graveyard" is any player's — [Targets.CardInGraveyard] is zone-scoped, not owner-scoped.
 */
val Sewerdreg = card("Sewerdreg") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Spirit"
    oracleText = "Swampwalk (This creature can't be blocked as long as defending player controls a Swamp.)\n" +
        "Sacrifice this creature: Exile target card from a graveyard."
    power = 3
    toughness = 3

    keywords(Keyword.SWAMPWALK)

    activatedAbility {
        cost = Costs.SacrificeSelf
        val t = target("target card from a graveyard", Targets.CardInGraveyard)
        effect = Effects.Exile(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "104"
        artist = "Joel Thomas"
        flavorText = "They hardly have form, dripping through pipe and grate with the slip and stench of flowing sewage."
        imageUri = "https://cards.scryfall.io/normal/front/b/2/b216ad4e-29b4-4e03-8c16-5796277bd05f.jpg"
    }
}
