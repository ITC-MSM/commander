package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Escape from Orthanc
 * {W}
 * Instant
 *
 * Target creature gets +1/+3 and gains flying until end of turn. Untap it.
 */
val EscapeFromOrthanc = card("Escape from Orthanc") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Target creature gets +1/+3 and gains flying until end of turn. Untap it."

    spell {
        val creature = target("creature", Targets.Creature)
        effect = Effects.ModifyStats(1, 3, creature)
            .then(Effects.GrantKeyword(Keyword.FLYING, creature))
            .then(Effects.Untap(creature))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "12"
        artist = "Anthony Devine"
        flavorText = "\"How far can you bear me?\" asked Gandalf.\n\"Many leagues,\" said Gwaihir, \"but not to the ends of the earth. I was sent to bear tidings, not burdens.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f4ff3330-dcca-4435-87e7-871be91a68b0.jpg?1686967744"
    }
}
