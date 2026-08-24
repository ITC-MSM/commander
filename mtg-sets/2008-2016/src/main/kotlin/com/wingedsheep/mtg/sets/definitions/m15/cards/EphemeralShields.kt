package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ephemeral Shields
 * {1}{W}
 * Instant
 * Convoke
 * Target creature gains indestructible until end of turn.
 */
val EphemeralShields = card("Ephemeral Shields") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText =
        "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Target creature gains indestructible until end of turn. (Damage and effects that say \"destroy\" don't destroy it.)"

    keywords(Keyword.CONVOKE)

    spell {
        val t = target("target creature", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "11"
        artist = "Yohann Schepacz"
        imageUri = "https://cards.scryfall.io/normal/front/0/4/04f9c2cc-2516-43d5-a7dc-27509f402077.jpg?1783939203"
    }
}
