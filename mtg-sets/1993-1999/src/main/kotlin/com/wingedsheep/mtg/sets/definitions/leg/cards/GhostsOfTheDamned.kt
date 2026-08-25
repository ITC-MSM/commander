package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ghosts of the Damned
 * {1}{B}{B}
 * Creature — Spirit
 * 0/2
 *
 * {T}: Target creature gets -1/-0 until end of turn.
 */
val GhostsOfTheDamned = card("Ghosts of the Damned") {
    manaCost = "{1}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Spirit"
    power = 0
    toughness = 2
    oracleText = "{T}: Target creature gets -1/-0 until end of turn."

    activatedAbility {
        cost = Costs.Tap
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(-1, 0, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "98"
        artist = "Edward P. Beard, Jr."
        flavorText = "The voices of the dead ring in the heart long after they have faded from the ears."
        imageUri = "https://cards.scryfall.io/normal/front/2/0/20275678-3488-43d8-a93b-993e2267ab07.jpg?1783948067"
    }
}
