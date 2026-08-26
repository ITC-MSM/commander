package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Violent Impact
 * {3}{R}
 * Sorcery
 * Destroy target artifact or land.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 */
val ViolentImpact = card("Violent Impact") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Destroy target artifact or land.\n" +
            "Cycling {2} ({2}, Discard this card: Draw a card.)"

    spell {
        val t = target("target", Targets.ArtifactOrLand)
        effect = Effects.Destroy(t)
    }

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "154"
        artist = "Jason Rainville"
        flavorText = "Initiates in the heat of combat must be able to adapt to changing conditions."
        imageUri = "https://cards.scryfall.io/normal/front/4/9/49b8673a-ce93-4881-b712-8db82446d83c.jpg?1783936481"
    }
}
