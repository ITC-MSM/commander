package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Acid Rain
 * {3}{U}
 * Sorcery
 *
 * Destroy all Forests.
 */
val AcidRain = card("Acid Rain") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Destroy all Forests."

    spell {
        effect = Effects.DestroyAll(GameObjectFilter.Land.withSubtype(Subtype.FOREST))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "44"
        artist = "NéNé Thomas"
        imageUri = "https://cards.scryfall.io/normal/front/b/a/ba93c50a-2440-4e92-9cba-d97e20b1d29c.jpg?1783948079"
    }
}
