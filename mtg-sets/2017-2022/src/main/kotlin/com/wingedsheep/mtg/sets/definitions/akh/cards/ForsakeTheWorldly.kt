package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Forsake the Worldly
 * {2}{W}
 * Instant
 * Exile target artifact or enchantment.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 */
val ForsakeTheWorldly = card("Forsake the Worldly") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Exile target artifact or enchantment.\n" +
            "Cycling {2} ({2}, Discard this card: Draw a card.)"

    spell {
        val t = target("target", Targets.ArtifactOrEnchantment)
        effect = Effects.Exile(t)
    }

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "13"
        artist = "Steve Argyle"
        flavorText = "\"Why cling to these trappings? They are but tools and affectations. True wealth can be possessed only in the afterlife.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cca4e95e-f14e-4cfa-918a-cfb15f912293.jpg?1783936539"
    }
}
