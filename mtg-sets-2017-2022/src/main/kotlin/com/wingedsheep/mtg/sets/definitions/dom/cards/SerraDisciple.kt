package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Serra Disciple
 * {1}{W}
 * Creature — Bird Cleric
 * 1/1
 * Flying, first strike
 * Whenever you cast a historic spell, Serra Disciple gets +1/+1 until end of turn.
 * (Artifacts, legendaries, and Sagas are historic.)
 */
val SerraDisciple = card("Serra Disciple") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bird Cleric"
    power = 1
    toughness = 1
    oracleText = "Flying, first strike\nWhenever you cast a historic spell, Serra Disciple gets +1/+1 until end of turn. (Artifacts, legendaries, and Sagas are historic.)"

    keywords(Keyword.FLYING, Keyword.FIRST_STRIKE)

    triggeredAbility {
        trigger = Triggers.YouCastHistoric
        effect = ModifyStatsEffect(1, 1, EffectTarget.Self, Duration.EndOfTurn)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "34"
        artist = "Victor Adame Minguez"
        imageUri = "https://cards.scryfall.io/normal/front/8/3/8359c3a8-d826-4326-a899-9f60ca774308.jpg?1562738741"
    }
}
