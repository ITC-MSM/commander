package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.model.Rarity

/**
 * Rakshasa's Bargain
 * {2/B}{2/G}{2/U}
 * Instant
 *
 * Look at the top four cards of your library. Put two of them into your hand and the rest
 * into your graveyard.
 *
 * The classic Sultai "dig" — modeled with [Patterns.Library.lookAtTopAndKeep] (count = 4,
 * keepCount = 2). The keeper cards go to hand and the remainder to the graveyard. Each of the
 * three hybrid mana symbols can be paid with two generic or one of the respective color.
 */
val RakshasasBargain = card("Rakshasa's Bargain") {
    manaCost = "{2/B}{2/G}{2/U}"
    colorIdentity = "BGU"
    typeLine = "Instant"
    oracleText = "Look at the top four cards of your library. Put two of them into your hand " +
        "and the rest into your graveyard."

    spell {
        effect = Patterns.Library.lookAtTopAndKeep(
            count = 4,
            keepCount = 2
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "214"
        artist = "Yigit Koroglu"
        flavorText = "\"You know you shouldn't and the Sultai forbid you. But you can't resist " +
            "the temptation, can you?\""
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5c409f4f-3b2c-4c33-b850-55b2a46f51ca.jpg?1743204844"
    }
}
