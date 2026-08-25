package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Jungle Weaver
 * {5}{G}{G}
 * Creature — Spider
 * 5 / 6
 * Reach (This creature can block creatures with flying.)
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 *
 * Vanilla body plus two keywords, and the split between them is the point: reach is a plain
 * evasion-defeating keyword ([Keyword.REACH] via `keywords`), while cycling carries a cost and so is
 * a [KeywordAbility.cycling] entry rather than a bare keyword — it lowers to the discard-for-a-card
 * activated ability the reminder text spells out, playable only from hand.
 */
val JungleWeaver = card("Jungle Weaver") {
    manaCost = "{5}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Spider"
    power = 5
    toughness = 6
    oracleText = "Reach (This creature can block creatures with flying.)\n" +
        "Cycling {2} ({2}, Discard this card: Draw a card.)"

    keywords(Keyword.REACH)

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "134"
        artist = "Trevor Hairsine"
        flavorText = "Weavers' webs wall off swaths of territory more effectively than any portcullis made of iron."
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5f46870b-3d8d-4aae-8d59-6bd88a50b37c.jpg"
    }
}
