package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Firja, Judge of Valor
 * {2}{W}{B}{B}
 * Legendary Creature — Angel Cleric
 * 2/4
 * Flying, lifelink
 * Whenever you cast your second spell each turn, look at the top three cards of your library. Put one of them into your hand and the rest into your graveyard.
 *
 * The payoff is [Patterns.Library.lookAtTopAndKeep] at its defaults — gather three, choose exactly
 * one for hand, remainder to the graveyard. The "second spell" ordinal is [Triggers.NthSpellCast]
 * with n = 2 scoped to [Player.You]; the engine already tracks each player's per-turn cast count.
 */
val FirjaJudgeOfValor = card("Firja, Judge of Valor") {
    manaCost = "{2}{W}{B}{B}"
    colorIdentity = "BW"
    typeLine = "Legendary Creature — Angel Cleric"
    oracleText = "Flying, lifelink\n" +
        "Whenever you cast your second spell each turn, look at the top three cards of your library. Put one of them into your hand and the rest into your graveyard."
    power = 2
    toughness = 4

    keywords(Keyword.FLYING, Keyword.LIFELINK)

    triggeredAbility {
        trigger = Triggers.NthSpellCast(2, Player.You)
        effect = Patterns.Library.lookAtTopAndKeep(count = 3, keepCount = 1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "209"
        artist = "Livia Prima"
        flavorText = "She is both shepherd and reaper, and her judgment is final."
        imageUri = "https://cards.scryfall.io/normal/front/d/f/df87077c-85d8-499e-bce0-27697caada5a.jpg"
    }
}
