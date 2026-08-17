package com.wingedsheep.mtg.sets.definitions.s99.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Thunder Dragon
 * {5}{R}{R}
 * Creature — Dragon
 * 5/5
 * Flying
 * When this creature enters, it deals 3 damage to each creature without flying.
 *
 * A one-sided sweeper in an ETB slot: [Patterns.Group.dealDamageToAll] over
 * [GroupFilter.AllCreatures]`.withoutKeyword(FLYING)` is the same iteration Earthquake uses, so the
 * "without flying" clause is a filter predicate rather than any new damage vocabulary — and the
 * Dragon's own flying keeps it out of its own blast.
 */
val ThunderDragon = card("Thunder Dragon") {
    manaCost = "{5}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dragon"
    power = 5
    toughness = 5
    oracleText = "Flying\n" +
        "When this creature enters, it deals 3 damage to each creature without flying."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Group.dealDamageToAll(3, GroupFilter.AllCreatures.withoutKeyword(Keyword.FLYING))
        description = "When this creature enters, it deals 3 damage to each creature without flying."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "119"
        artist = "Dana Knutson"
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7e9b06a8-c3f3-4174-b992-7da7ca163990.jpg?1783946025"
    }
}
