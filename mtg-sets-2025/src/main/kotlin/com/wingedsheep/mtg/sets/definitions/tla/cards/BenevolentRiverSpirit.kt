package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Benevolent River Spirit
 * {U}{U}
 * Creature — Spirit
 * 4/5
 * As an additional cost to cast this spell, waterbend {5}. (While paying a waterbend cost, you
 * can tap your artifacts and creatures to help. Each one pays for {1}.)
 * Flying, ward {2}
 * When this creature enters, scry 2.
 */
val BenevolentRiverSpirit = card("Benevolent River Spirit") {
    manaCost = "{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit"
    oracleText = "As an additional cost to cast this spell, waterbend {5}. " +
        "(While paying a waterbend cost, you can tap your artifacts and creatures to help. " +
        "Each one pays for {1}.)\n" +
        "Flying, ward {2} (Whenever this creature becomes the target of a spell or ability an " +
        "opponent controls, counter it unless that player pays {2}.)\n" +
        "When this creature enters, scry 2."
    power = 4
    toughness = 5

    waterbendCost(amount = 5)
    keywords(Keyword.FLYING)
    keywordAbility(KeywordAbility.ward("{2}"))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.scry(2)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "45"
        artist = "Mizutametori"
        imageUri = "https://cards.scryfall.io/normal/front/7/f/7ffb79cd-d170-4047-89c8-6e85188f30da.jpg?1764120194"
    }
}
