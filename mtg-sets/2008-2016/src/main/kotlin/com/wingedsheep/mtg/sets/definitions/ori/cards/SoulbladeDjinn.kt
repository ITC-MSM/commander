package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Soulblade Djinn
 * {3}{U}{U}
 * Creature — Djinn
 * 4/3
 *
 * Flying
 * Whenever you cast a noncreature spell, creatures you control get +1/+1 until end of turn.
 *
 * Prowess for the whole team — the same cast trigger, but the body is a group pump rather than a
 * self pump, so it is spelled out rather than reusing the `prowess()` shorthand.
 */
val SoulbladeDjinn = card("Soulblade Djinn") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Djinn"
    oracleText = "Flying\n" +
        "Whenever you cast a noncreature spell, creatures you control get +1/+1 until end of turn."
    power = 4
    toughness = 3

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = Patterns.Group.modifyStatsForAll(1, 1, Filters.Group.creaturesYouControl)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "75"
        artist = "Viktor Titov"
        flavorText = "He grants endless wishes, as long as you always wish for a blade."
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e2a48455-dde4-4263-9146-af547c0aad48.jpg"
    }
}
