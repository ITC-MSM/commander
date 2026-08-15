package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Shadow Summoning
 * {W}{B}
 * Sorcery
 *
 * Create two tapped 1/1 white Spirit creature tokens with flying.
 */
val ShadowSummoning = card("Shadow Summoning") {
    manaCost = "{W}{B}"
    colorIdentity = "BW"
    typeLine = "Sorcery"
    oracleText = "Create two tapped 1/1 white Spirit creature tokens with flying."

    spell {
        effect = CreateTokenEffect(
            count = DynamicAmount.Fixed(2),
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Spirit"),
            keywords = setOf(Keyword.FLYING),
            tapped = true,
            imageUri = "https://cards.scryfall.io/normal/front/b/5/b53ee2f0-afbd-491f-b3b8-c9997f175199.jpg?1699974536"
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "226"
        artist = "Campbell White"
        flavorText = "\"Whose shall the horn be? Who shall call them from the grey twilight, the forgotten people? The heir of him to whom the oath they swore.\"\n—Malbeth the Seer"
        imageUri = "https://cards.scryfall.io/normal/front/e/c/ec0984b2-bed6-41b1-9087-2cfd16749037.jpg?1686970017"
    }
}
