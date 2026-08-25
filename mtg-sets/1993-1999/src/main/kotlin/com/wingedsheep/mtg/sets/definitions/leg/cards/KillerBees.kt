package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Killer Bees
 * {1}{G}{G}
 * Creature — Insect
 * 0/1
 *
 * Flying
 * {G}: This creature gets +1/+1 until end of turn.
 */
val KillerBees = card("Killer Bees") {
    manaCost = "{1}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Insect"
    power = 0
    toughness = 1
    oracleText = "Flying\n{G}: This creature gets +1/+1 until end of turn."

    keywords(Keyword.FLYING)
    activatedAbility {
        cost = Costs.Mana("{G}")
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "192"
        artist = "Phil Foglio"
        flavorText = "The communal mind produces a savage strategy, yet no one could predict that this vicious " +
            "crossbreed would unravel the secret of steel."
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2e30b5ff-1239-4c4d-ac7c-554ecf8e1e27.jpg?1783948046"
    }
}
