package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CanAttackDespiteDefender

/**
 * Mechan Shieldmate — see `oracleText` for the full rules text.
 *
 * Ruling (Scryfall, WotC 2025-07-25): once an artifact enters the battlefield under your
 * control, Mechan Shieldmate can attack that turn — it does not matter whether that artifact
 * stays an artifact or stays under your control afterwards. That's why the condition reads
 * an ETB-by-type *event* tracker rather than the current battlefield population.
 */
val MechanShieldmate = card("Mechan Shieldmate") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Artifact Creature — Robot Soldier"
    power = 3
    toughness = 2
    oracleText = "Defender\nAs long as an artifact entered the battlefield under your control this turn, this creature can attack as though it didn't have defender."

    keywords(Keyword.DEFENDER)

    staticAbility {
        ability = CanAttackDespiteDefender(
            condition = Conditions.ArtifactEnteredBattlefieldThisTurn
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "65"
        artist = "Daniel Ljunggren"
        flavorText = "\"Do not mistake a preference for peace for an inability to fight.\"\n—Yvin, Pinnacle envoy"
        imageUri = "https://cards.scryfall.io/normal/front/7/4/745b2119-4d9f-431f-89b9-10ad48b6dc47.jpg?1752946812"
    }
}
