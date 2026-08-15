package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CanAttackDespiteDefender

/**
 * Shipwreck Sentry
 * {1}{U}
 * Creature — Human Pirate
 * 3/3
 * Defender
 * As long as an artifact entered the battlefield under your control this turn, this creature
 * can attack as though it didn't have defender.
 *
 * Functional twin of Mechan Shieldmate (EOE) — same {1}{U}, Defender, and artifact-entered
 * gate. The condition is a pure ETB *event* tracker
 * ([Conditions.ArtifactEnteredBattlefieldThisTurn]): once an artifact entered under your
 * control this turn, the defender restriction is bypassed for the rest of the turn, even if
 * that artifact has since left the battlefield or stopped being an artifact.
 */
val ShipwreckSentry = card("Shipwreck Sentry") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Pirate"
    power = 3
    toughness = 3
    oracleText = "Defender\nAs long as an artifact entered the battlefield under your control this turn, this creature can attack as though it didn't have defender."

    keywords(Keyword.DEFENDER)

    staticAbility {
        ability = CanAttackDespiteDefender(
            condition = Conditions.ArtifactEnteredBattlefieldThisTurn
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "75"
        artist = "Ryan Valle"
        flavorText = "\"That's close enough, stranger.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/1/814803bc-72cd-46db-b957-4322f2a7b28a.jpg?1782694549"
    }
}
