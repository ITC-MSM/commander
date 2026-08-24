package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Anarchy
 * {2}{R}{R}
 * Sorcery
 *
 * Destroy all white permanents.
 *
 * `Effects.DestroyAll` rather than an iteration: the sweep lowers to the gather-then-move pipeline,
 * whose gather reads the battlefield through *projected* state, so a permanent made white by a
 * continuous effect is caught and one whose colour was changed away is not.
 */
val Anarchy = card("Anarchy") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Destroy all white permanents."

    spell {
        effect = Effects.DestroyAll(GameObjectFilter.Permanent.withColor(Color.WHITE))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "170"
        artist = "Phil Foglio"
        flavorText = "\"The Shaman waved the staff, and the land itself went mad.\"\n—Disa the Restless, journal entry"
        imageUri = "https://cards.scryfall.io/normal/front/2/8/28d941da-b5cb-4b7e-84f2-ece883f89af3.jpg"
    }
}
