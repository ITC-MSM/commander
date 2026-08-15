package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Second Breakfast
 * {2}{W}
 * Instant
 *
 * Up to two target creatures each get +2/+1 until end of turn. Create a Food token.
 */
val SecondBreakfast = card("Second Breakfast") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Up to two target creatures each get +2/+1 until end of turn. Create a Food token. (It's an artifact with \"{2}, {T}, Sacrifice this token: You gain 3 life.\")"

    spell {
        val (c1, c2) = targets("creature", TargetCreature(count = 2, optional = true))
        effect = Effects.ModifyStats(2, 1, c1)
            .then(Effects.ModifyStats(2, 1, c2))
            .then(Effects.CreateFood())
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "29"
        artist = "Christina Kraus"
        flavorText = "The Hobbits of the Shire were fond of six meals a day—when they could get them."
        imageUri = "https://cards.scryfall.io/normal/front/0/0/002f647a-25f8-461b-8617-52674f5d577c.jpg?1686967911"
    }
}
