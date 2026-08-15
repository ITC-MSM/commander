package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Lost Days
 * {4}{U}
 * Instant — Lesson
 *
 * The owner of target creature or enchantment puts it into their library second
 * from the top or on the bottom. You create a Clue token. (It's an artifact with
 * "{2}, Sacrifice this token: Draw a card.")
 */
val LostDays = card("Lost Days") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Instant — Lesson"
    oracleText = "The owner of target creature or enchantment puts it into their library second " +
        "from the top or on the bottom. You create a Clue token. (It's an artifact with " +
        "\"{2}, Sacrifice this token: Draw a card.\")"

    spell {
        val t = target("target creature or enchantment", Targets.CreatureOrEnchantment)
        effect = Effects.Composite(
            Effects.PutSecondFromTopOrBottomOfLibrary(t),
            Effects.CreateClue(),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "62"
        artist = "Matteo Bassini"
        flavorText = "At every step, his friend was just out of reach."
        imageUri = "https://cards.scryfall.io/normal/front/d/b/db8e88e3-0931-403f-90e7-3c22c0b61dac.jpg?1764120357"
    }
}
