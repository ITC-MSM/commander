package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Balamb T-Rexaur
 * {4}{G}{G}
 * Creature — Dinosaur
 * 6/6
 * Trample
 * When this creature enters, you gain 3 life.
 * Forestcycling {2} ({2}, Discard this card: Search your library for a Forest card,
 * reveal it, put it into your hand, then shuffle.)
 *
 * Forestcycling is basic-land typecycling for Forest ([KeywordAbility.typecycling]).
 */
val BalambTRexaur = card("Balamb T-Rexaur") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dinosaur"
    power = 6
    toughness = 6
    oracleText = "Trample\nWhen this creature enters, you gain 3 life.\nForestcycling {2} " +
        "({2}, Discard this card: Search your library for a Forest card, reveal it, " +
        "put it into your hand, then shuffle.)"

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(3)
    }

    keywordAbility(KeywordAbility.typecycling("Forest", ManaCost.parse("{2}")))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "173"
        artist = "Fang Xinyu"
        flavorText = "\"Sometimes it's better to run!\"\n—Quistis Trepe"
        imageUri = "https://cards.scryfall.io/normal/front/e/5/e5857b1b-73bc-458e-b26b-7ed8bef785f3.jpg?1748706407"
    }
}
