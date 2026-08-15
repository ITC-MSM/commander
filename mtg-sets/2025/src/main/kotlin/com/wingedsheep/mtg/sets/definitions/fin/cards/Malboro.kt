package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Malboro
 * {4}{B}{B}
 * Creature — Plant Horror
 * 4/4
 * Bad Breath — When this creature enters, each opponent discards a card, loses 2 life, and
 * exiles the top three cards of their library.
 * Swampcycling {2} ({2}, Discard this card: Search your library for a Swamp card, reveal it,
 * put it into your hand, then shuffle.)
 *
 * The ETB acts on *each* opponent individually, so it is modeled with
 * [Effects.ForEachPlayer] over [Player.EachOpponent]: within each iteration `Player.You`
 * rebinds to that opponent, so the discard / life loss / exile-top all target the player
 * being processed. Swampcycling is Typecycling for the Swamp land type.
 */
val Malboro = card("Malboro") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Plant Horror"
    power = 4
    toughness = 4
    oracleText = "Bad Breath — When this creature enters, each opponent discards a card, loses 2 life, " +
        "and exiles the top three cards of their library.\n" +
        "Swampcycling {2} ({2}, Discard this card: Search your library for a Swamp card, reveal it, " +
        "put it into your hand, then shuffle.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.ForEachPlayer(
            Player.EachOpponent,
            listOf(
                Patterns.Hand.discardCards(1, EffectTarget.Controller),
                Effects.LoseLife(2, EffectTarget.Controller),
                Patterns.Library.exileTop(3, EffectTarget.Controller),
            ),
        )
    }

    keywordAbility(KeywordAbility.typecycling("Swamp", ManaCost.parse("{2}")))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "106"
        artist = "Dan Watson"
        imageUri = "https://cards.scryfall.io/normal/front/e/4/e46d8048-03ce-4e07-ba24-f41ba6140a4e.jpg?1748706158"
    }
}
