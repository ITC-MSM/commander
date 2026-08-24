package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Broodmate Dragon
 * {3}{B}{R}{G}
 * Creature — Dragon
 * 4 / 4
 * Flying
 * When this creature enters, create a 4/4 red Dragon creature token with flying.
 *
 * A plain [Triggers.EntersBattlefield] (SELF binding) over a single [Effects.CreateToken] — one
 * token, the mate; the pair of Dragons on the board is the Broodmate plus its token, not two
 * tokens. The token's flying is a keyword on the token itself rather than a granted continuous
 * effect, so it survives the trigger finishing resolution.
 */
val BroodmateDragon = card("Broodmate Dragon") {
    manaCost = "{3}{B}{R}{G}"
    colorIdentity = "BGR"
    typeLine = "Creature — Dragon"
    power = 4
    toughness = 4
    oracleText = "Flying\n" +
        "When this creature enters, create a 4/4 red Dragon creature token with flying."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 4,
            toughness = 4,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Dragon"),
            keywords = setOf(Keyword.FLYING),
            imageUri = "https://cards.scryfall.io/normal/front/c/b/cbb3e91d-9cae-49c8-ba92-087ad8171b9d.jpg"
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "160"
        artist = "Vance Kovacs"
        flavorText = "Frozen in fear, the goblins stared upward at the circling hunter—and were promptly eaten by its diving mate."
        imageUri = "https://cards.scryfall.io/normal/front/a/e/aea1360e-7c6b-400c-893a-82d93e53101e.jpg"
    }
}
