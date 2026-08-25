package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sheltering Light
 * {W}
 * Instant
 *
 * Target creature gains indestructible until end of turn. Scry 1.
 */
val ShelteringLight = card("Sheltering Light") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Target creature gains indestructible until end of turn. Scry 1. " +
        "(Damage and effects that say \"destroy\" don't destroy the creature.)"

    spell {
        val protege = target("target", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, protege) then Effects.Scry(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "35"
        artist = "Gabor Szikszai"
        flavorText = "Those who wield the power of the sun protect the Empire from darkness."
        imageUri = "https://cards.scryfall.io/normal/front/b/0/b0c945c7-6c31-4c4f-9203-3dc2aef50820.jpg"
    }
}
