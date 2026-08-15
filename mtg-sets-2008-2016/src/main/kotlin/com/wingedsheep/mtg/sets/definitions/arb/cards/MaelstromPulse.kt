package com.wingedsheep.mtg.sets.definitions.arb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.namedFromVariable
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource

/**
 * Maelstrom Pulse
 * {1}{B}{G}
 * Sorcery
 *
 * Destroy target nonland permanent and all other permanents with the same name as
 * that permanent.
 *
 * Resolution pipeline: gather the chosen target, capture its card name, then gather
 * every battlefield permanent sharing that name (via
 * [GameObjectFilter.namedFromVariable]) and destroy the whole group. The target
 * shares its own name, so it is destroyed along with its copies.
 */
val MaelstromPulse = card("Maelstrom Pulse") {
    manaCost = "{1}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Sorcery"
    oracleText = "Destroy target nonland permanent and all other permanents with the same name as that permanent."

    spell {
        target("target nonland permanent", Targets.NonlandPermanent)
        effect = Effects.Pipeline {
            val chosen = gather(CardSource.ChosenTargets, name = "target")
            val chosenName = storeCardName(chosen, name = "name")
            val sameNamed = gather(
                GameObjectFilter.Any.namedFromVariable(chosenName),
                name = "sameNamed"
            )
            destroy(sameNamed)
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "92"
        artist = "Anthony Francisco"
        imageUri = "https://cards.scryfall.io/normal/front/e/b/eb651c3a-cb27-4b73-8eb6-b87d65211097.jpg?1782715800"
    }
}
