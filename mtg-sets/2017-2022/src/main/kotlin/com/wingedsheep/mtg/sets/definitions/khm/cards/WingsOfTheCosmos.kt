package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wings of the Cosmos
 * {W}
 * Instant
 * Target creature gets +1/+3 and gains flying until end of turn. Untap it.
 *
 * A one-mana trick that does three things to one creature. The untap is a separate sentence on the
 * card and a separate effect here — it lets a tapped attacker block, which the +1/+3 and flying then
 * make survivable.
 */
val WingsOfTheCosmos = card("Wings of the Cosmos") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Target creature gets +1/+3 and gains flying until end of turn. Untap it."

    spell {
        val recipient = target("target creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(1, 3, recipient),
            Effects.GrantKeyword(Keyword.FLYING, recipient),
            Effects.Untap(recipient)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "39"
        artist = "Ilse Gort"
        flavorText = "The wolf's startled yelp changed quickly to a howl of elation as she soared over her envious packmates."
        imageUri = "https://cards.scryfall.io/normal/front/d/6/d6b26c95-f90d-43fb-8c99-2a3aa13ac2c6.jpg"
    }
}
