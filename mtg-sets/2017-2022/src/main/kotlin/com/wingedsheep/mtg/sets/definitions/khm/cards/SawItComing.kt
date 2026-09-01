package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Saw It Coming
 * {1}{U}{U}
 * Instant
 * Counter target spell.
 * Foretell {1}{U} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)
 *
 * Kaldheim's Cancel: foretold on an earlier turn it holds up only {1}{U}, and the face-down exile
 * hides which answer is coming.
 */
val SawItComing = card("Saw It Coming") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target spell.\n" +
        "Foretell {1}{U} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)"

    spell {
        target = Targets.Spell
        effect = Effects.CounterSpell()
    }

    keywordAbility(KeywordAbility.foretell("{1}{U}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "76"
        artist = "Randy Vargas"
        flavorText = "\"How predictable.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/7/877a1bb9-5eae-453a-bec0-a9de20ea6815.jpg"
    }
}
