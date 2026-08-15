package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Rout
 * {3}{W}{W}
 * Sorcery
 * You may cast this spell as though it had flash if you pay {2} more to cast it.
 * Destroy all creatures. They can't be regenerated.
 *
 * The "pay {2} more to cast as though it had flash" clause is the Breaking Wave /
 * Ghitu Fire pattern: [KeywordAbility.flashKicker] — paying the extra cost unlocks
 * instant-speed casting without otherwise changing the spell.
 */
val Rout = card("Rout") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "You may cast this spell as though it had flash if you pay {2} more to cast it. " +
        "(You may cast it any time you could cast an instant.)\n" +
        "Destroy all creatures. They can't be regenerated."

    keywordAbility(KeywordAbility.flashKicker("{2}"))

    spell {
        effect = Effects.DestroyAll(
            filter = GameObjectFilter.Creature,
            noRegenerate = true,
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "34"
        artist = "Ron Spencer"
        imageUri = "https://cards.scryfall.io/normal/front/9/4/94bc55ed-b89b-4e22-b3f1-4ce0f8d180d7.jpg?1562924999"
    }
}
