package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Gods' Hall Guardian
 * {5}{W}
 * Creature — Cat
 * 3/6
 * Vigilance
 * Foretell {3}{W} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)
 *
 * Foretell is the card's only structural part — the printed `Keyword.FORETELL` is display-only,
 * so it is lowered into [KeywordAbility.foretell], which the engine's ForetellEnumerator reads to
 * offer the pay-{2}-and-exile special action and the later cast from exile for the foretell cost.
 * The card builder derives the printed keyword back out of that ability.
 */
val GodsHallGuardian = card("Gods' Hall Guardian") {
    manaCost = "{5}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat"
    oracleText = "Vigilance\n" +
        "Foretell {3}{W} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)"
    power = 3
    toughness = 6

    keywords(Keyword.VIGILANCE)
    keywordAbility(KeywordAbility.foretell("{3}{W}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "13"
        artist = "Sidharth Chaturvedi"
        flavorText = "Not a single rat has been seen in Istfell since the gods moved in."
        imageUri = "https://cards.scryfall.io/normal/front/e/8/e8e645c8-90ac-4865-b441-e64251d6c9a8.jpg"
    }
}
