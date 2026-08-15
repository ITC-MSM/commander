package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Walltop Sentries
 * {2}{G}
 * Creature — Human Soldier Ally
 * 2/3
 *
 * Reach, deathtouch
 * When this creature dies, if there's a Lesson card in your graveyard, you gain 2 life.
 */
val WalltopSentries = card("Walltop Sentries") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Soldier Ally"
    power = 2
    toughness = 3
    oracleText = "Reach, deathtouch\n" +
        "When this creature dies, if there's a Lesson card in your graveyard, you gain 2 life."

    keywords(Keyword.REACH, Keyword.DEATHTOUCH)

    triggeredAbility {
        trigger = Triggers.Dies
        triggerCondition = Conditions.GraveyardContainsSubtype(Subtype.LESSON)
        effect = Effects.GainLife(2)
        description = "When this creature dies, if there's a Lesson card in your graveyard, you gain 2 life."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "202"
        artist = "Boell Oyino"
        flavorText = "\"That drill shall not reach Ba Sing Se! This is what we trained for!\""
        imageUri = "https://cards.scryfall.io/normal/front/e/5/e55f1c63-58f6-4e2b-aaeb-f2d5faca47a2.jpg?1764122900"
    }
}
