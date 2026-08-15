package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect

/**
 * Bombadil's Song
 * {1}{G}
 * Instant
 *
 * Target creature you control gets +1/+1 and gains hexproof until end of turn. The Ring tempts you.
 */
val BombadilsSong = card("Bombadil's Song") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Target creature you control gets +1/+1 and gains hexproof until end of turn. The Ring tempts you. " +
        "(A creature with hexproof can't be the target of spells or abilities your opponents control.)"

    spell {
        val creature = target("creature you control", Targets.CreatureYouControl)
        effect = ModifyStatsEffect(
            powerModifier = 1,
            toughnessModifier = 1,
            target = creature,
            duration = Duration.EndOfTurn
        )
            .then(Effects.GrantKeyword(Keyword.HEXPROOF, creature, Duration.EndOfTurn))
            .then(Effects.TheRingTemptsYou())
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "154"
        artist = "Marie Magny"
        flavorText = "\"Hey dol! merry dol! ring a dong dillo!\n" +
            "Ring a dong! hop along! fal lal the willow!\n" +
            "Tom Bom, jolly Tom, Tom Bombadillo!\""
        imageUri = "https://cards.scryfall.io/normal/front/6/3/6391699e-987a-499f-9fec-96f2362760b9.jpg?1687694677"
    }
}
