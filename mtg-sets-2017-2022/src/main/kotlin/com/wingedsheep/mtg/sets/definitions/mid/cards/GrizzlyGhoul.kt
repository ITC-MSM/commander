package com.wingedsheep.mtg.sets.definitions.mid.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Grizzly Ghoul
 * {2}{B}{G}
 * Creature — Zombie Bear
 * 4/3
 * Trample
 * This creature enters with a +1/+1 counter on it for each creature that died this turn.
 *
 * "Each creature that died this turn" is controller-agnostic, so the counter count reads
 * [Player.Each] (creatures that died under any player's control this turn).
 */
val GrizzlyGhoul = card("Grizzly Ghoul") {
    manaCost = "{2}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Zombie Bear"
    power = 4
    toughness = 3
    oracleText = "Trample\n" +
        "This creature enters with a +1/+1 counter on it for each creature that died this turn."

    keywords(Keyword.TRAMPLE)

    replacementEffect(
        EntersWithDynamicCounters(
            count = DynamicAmounts.creaturesDiedThisTurn(Player.Each)
        )
    )

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "226"
        artist = "Vincent Proce"
        imageUri = "https://cards.scryfall.io/normal/front/e/e/eec421f1-4ee8-4816-ab5c-372e87aae231.jpg?1782703581"
    }
}
