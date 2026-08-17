package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Rob the Archives
 * {1}{R}
 * Sorcery
 * Casualty 1 (As you cast this spell, you may sacrifice a creature with power 1 or greater. When you do, copy this spell.)
 * Exile the top two cards of your library. You may play those cards this turn.
 *
 * Casualty 1 (CR 702.153) is the printed [KeywordAbility.Casualty]. The body is the plain impulse
 * draw — [Patterns.Exile.impulse] with count = 2 and the default end-of-turn play window.
 */
val RobTheArchives = card("Rob the Archives") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Casualty 1 (As you cast this spell, you may sacrifice a creature with power 1 or greater. When you do, copy this spell.)\nExile the top two cards of your library. You may play those cards this turn."

    keywordAbility(KeywordAbility.casualty(1))

    spell {
        effect = Patterns.Exile.impulse(count = 2)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "122"
        artist = "Steve Argyle"
        flavorText = "\"Well, I guess stealth's out of the question.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a3ec95f6-88c8-4daf-882f-8b4bc73452c3.jpg?1783923112"
    }
}
