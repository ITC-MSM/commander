package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Ryusei, the Falling Star
 * {5}{R}
 * Legendary Creature — Dragon Spirit
 * 5/5
 * Flying
 * When Ryusei dies, it deals 5 damage to each creature without flying.
 *
 * Thunder Dragon's sweeper moved from the enters slot to the dies slot: [Triggers.Dies] plus
 * [Patterns.Group.dealDamageToAll] over [GroupFilter.AllCreatures]`.withoutKeyword(FLYING)`. The
 * loop reads nothing off Ryusei itself — each damage instance targets the iterated creature — so
 * the ability needs no last-known information about the source that has already left the
 * battlefield.
 */
val RyuseiTheFallingStar = card("Ryusei, the Falling Star") {
    manaCost = "{5}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Dragon Spirit"
    power = 5
    toughness = 5
    oracleText = "Flying\n" +
        "When Ryusei dies, it deals 5 damage to each creature without flying."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Patterns.Group.dealDamageToAll(5, GroupFilter.AllCreatures.withoutKeyword(Keyword.FLYING))
        description = "When Ryusei dies, it deals 5 damage to each creature without flying."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "185"
        artist = "Nottsuo"
        imageUri = "https://cards.scryfall.io/normal/front/1/7/17898412-6275-4762-a03b-04daf30fee7f.jpg?1783944296"
    }
}
