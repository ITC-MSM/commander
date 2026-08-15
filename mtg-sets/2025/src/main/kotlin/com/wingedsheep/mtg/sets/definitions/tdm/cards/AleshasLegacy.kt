package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Alesha's Legacy
 * {1}{B}
 * Instant
 * Target creature you control gains deathtouch and indestructible until end of turn.
 */
val AleshasLegacy = card("Alesha's Legacy") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature you control gains deathtouch and indestructible until end of turn. " +
        "(Damage and effects that say \"destroy\" don't destroy it.)"

    spell {
        val target = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.Composite(listOf(
            Effects.GrantKeyword(Keyword.DEATHTOUCH, target),
            Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, target),
        ))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "72"
        artist = "Craig J Spearing"
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a9262bf6-df6a-446c-ba70-18270a09842d.jpg?1743204249"
    }
}
