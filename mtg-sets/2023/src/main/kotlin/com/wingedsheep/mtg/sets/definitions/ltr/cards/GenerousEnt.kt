package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Generous Ent
 * {5}{G}
 * Creature — Treefolk
 * 5/7
 *
 * Reach
 * When this creature enters, create a Food token.
 * Forestcycling {1}
 */
val GenerousEnt = card("Generous Ent") {
    manaCost = "{5}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Treefolk"
    power = 5
    toughness = 7
    oracleText = "Reach\n" +
        "When this creature enters, create a Food token. (It's an artifact with \"{2}, {T}, Sacrifice this token: You gain 3 life.\")\n" +
        "Forestcycling {1} ({1}, Discard this card: Search your library for a Forest card, reveal it, put it into your hand, then shuffle.)"

    keywords(Keyword.REACH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateFood()
    }

    keywordAbility(KeywordAbility.typecycling("Forest", ManaCost.parse("{1}")))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "169"
        artist = "Simon Dominic"
        imageUri = "https://cards.scryfall.io/normal/front/8/5/85d22d5d-3875-42ff-b51e-c6e21db201f5.jpg?1687210970"
    }
}
