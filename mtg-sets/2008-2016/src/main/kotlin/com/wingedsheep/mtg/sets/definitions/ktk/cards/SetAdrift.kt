package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Set Adrift
 * {5}{U}
 * Sorcery
 * Delve
 * Put target nonland permanent on top of its owner's library.
 */
val SetAdrift = card("Set Adrift") {
    manaCost = "{5}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Delve (Each card you exile from your graveyard while casting this spell pays for {1}.)\nPut target nonland permanent on top of its owner's library."

    keywords(Keyword.DELVE)

    spell {
        val permanent = target("target", Targets.NonlandPermanent)
        effect = Effects.PutOnTopOfLibrary(permanent)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "54"
        artist = "Karl Kopinski"
        flavorText = "The envoy spoke, and Sidisi replied."
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cdad122d-a872-449f-9a7c-0f054b711d11.jpg?1562793718"
    }
}
