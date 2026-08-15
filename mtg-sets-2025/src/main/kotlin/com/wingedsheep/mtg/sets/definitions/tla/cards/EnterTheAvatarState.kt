package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration

/**
 * Enter the Avatar State
 * {W}
 * Instant — Lesson
 * Until end of turn, target creature you control becomes an Avatar in addition to its
 * other types and gains flying, first strike, lifelink, and hexproof. (A creature with
 * hexproof can't be the target of spells or abilities your opponents control.)
 */
val EnterTheAvatarState = card("Enter the Avatar State") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant — Lesson"
    oracleText = "Until end of turn, target creature you control becomes an Avatar in addition to its other types and gains flying, first strike, lifelink, and hexproof. (A creature with hexproof can't be the target of spells or abilities your opponents control.)"

    spell {
        val t = target("target", Targets.CreatureYouControl)
        effect = Effects.Composite(
            Effects.AddCreatureType("Avatar", t, Duration.EndOfTurn),
            Effects.GrantKeyword(Keyword.FLYING, t, Duration.EndOfTurn),
            Effects.GrantKeyword(Keyword.FIRST_STRIKE, t, Duration.EndOfTurn),
            Effects.GrantKeyword(Keyword.LIFELINK, t, Duration.EndOfTurn),
            Effects.GrantKeyword(Keyword.HEXPROOF, t, Duration.EndOfTurn)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "18"
        artist = "Shiren"
        flavorText = "\"The glow is the combination of all your past lives, focusing their energy through your body.\"\n—Avatar Roku"
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a8f3d2cb-d073-4df8-8769-49612628a377.jpg?1764119993"
    }
}
