package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.mobilize
import com.wingedsheep.sdk.model.Rarity

/**
 * Avenger of the Fallen — Tarkir: Dragonstorm #73
 * {2}{B} · Creature — Human Warrior · 2/4
 *
 * Deathtouch
 * Mobilize X, where X is the number of creature cards in your graveyard. (Whenever this
 * creature attacks, create X tapped and attacking 1/1 red Warrior creature tokens.
 * Sacrifice them at the beginning of the next end step.)
 *
 * The dynamic Mobilize is wired via the `mobilize(amount, ...)` DSL overload: it adds the
 * "Mobilize X" display keyword plus the attack-triggered token creation, with the count
 * supplied by [DynamicAmounts.creatureCardsInYourGraveyard] (DynamicAmount.Count over the
 * graveyard filtered to creature cards) resolved at attack time, not cast time.
 */
val AvengerOfTheFallen = card("Avenger of the Fallen") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Warrior"
    power = 2
    toughness = 4
    oracleText = "Deathtouch\n" +
        "Mobilize X, where X is the number of creature cards in your graveyard. (Whenever this " +
        "creature attacks, create X tapped and attacking 1/1 red Warrior creature tokens. " +
        "Sacrifice them at the beginning of the next end step.)"

    keywords(Keyword.DEATHTOUCH)

    mobilize(
        amount = DynamicAmounts.creatureCardsInYourGraveyard(),
        amountDescription = "X, where X is the number of creature cards in your graveyard",
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "73"
        artist = "Winona Nelson"
        imageUri = "https://cards.scryfall.io/normal/front/d/5/d5397366-151f-46b0-b9b2-fa4d5bd892d8.jpg?1743204251"
    }
}
