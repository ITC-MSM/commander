package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Mischievous Mystic
 * {1}{U}
 * Creature — Human Wizard
 * 2/1
 *
 * Flying
 * Whenever you draw your second card each turn, create a 1/1 blue Faerie creature token with flying.
 *
 * The draw trigger uses [Triggers.NthCardDrawn] (n = 2), the shared "your Nth card each turn"
 * detector, fired exactly once per turn when your second card is drawn.
 */
val MischievousMystic = card("Mischievous Mystic") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 1
    oracleText = "Flying\n" +
        "Whenever you draw your second card each turn, create a 1/1 blue Faerie creature token with flying."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.NthCardDrawn(2)
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLUE),
            creatureTypes = setOf("Faerie"),
            keywords = setOf(Keyword.FLYING),
            imageUri = "https://cards.scryfall.io/normal/front/d/1/d1c0556e-ba3c-4a8e-b704-8eaa7c4dba1c.jpg?1782727481",
        )
        description = "Whenever you draw your second card each turn, create a 1/1 blue Faerie creature token with flying."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "47"
        artist = "Steve Prescott"
        flavorText = "\"Don't worry about where they're taking me. The more pressing question is, why aren't they taking you?\""
        imageUri = "https://cards.scryfall.io/normal/front/2/0/20d89cec-528b-4b2a-87db-e11ce0000622.jpg?1782689225"
    }
}
