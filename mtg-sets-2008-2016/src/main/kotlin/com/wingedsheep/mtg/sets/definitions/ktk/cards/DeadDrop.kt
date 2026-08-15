package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Dead Drop
 * {9}{B}
 * Sorcery
 * Delve
 * Target player sacrifices two creatures.
 */
val DeadDrop = card("Dead Drop") {
    manaCost = "{9}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Delve (Each card you exile from your graveyard while casting this spell pays for {1}.)\nTarget player sacrifices two creatures."

    keywords(Keyword.DELVE)

    spell {
        val t = target("target", Targets.Player)
        effect = Effects.Sacrifice(GameObjectFilter.Creature, count = 2, target = t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "67"
        artist = "Greg Staples"
        flavorText = "\"Got a diving lesson\"—Sultai expression meaning \"was fed to the crocodiles\""
        imageUri = "https://cards.scryfall.io/normal/front/f/f/ff25a19a-8f98-47ef-847c-ba526c82b290.jpg?1562796664"
    }
}
