package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vanquish the Weak
 * {2}{B}
 * Instant
 * Destroy target creature with power 3 or less.
 *
 * Canonical printing: Ixalan, the card's earliest real-expansion printing. Reprinted in MOM as a
 * `Printing` row.
 */
val VanquishTheWeak = card("Vanquish the Weak") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Destroy target creature with power 3 or less."

    spell {
        val victim = target(
            "target creature with power 3 or less",
            Targets.CreatureWithPowerAtMost(3)
        )
        effect = Effects.Destroy(victim)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "127"
        artist = "David Palumbo"
        flavorText = "The clerics known as condemners punish those who do not recognize the " +
            "righteous authority of the church."
        imageUri = "https://cards.scryfall.io/normal/front/e/5/e599ed0b-4b3b-4341-b6ac-7fdfdc6799a3.jpg?1783935752"
    }
}
