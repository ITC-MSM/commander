package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Tezzeret's Strider
 * {3}
 * Artifact Creature — Golem
 * 3/1
 * As long as you control a Tezzeret planeswalker, this creature has menace. (It can't be blocked except by two or more creatures.)
 *
 * A [ConditionalStaticAbility] gating [GrantKeyword] over `GroupFilter.source()` — menace appears
 * and disappears with the Tezzeret, since the condition is re-evaluated at projection rather than
 * latched. "A Tezzeret planeswalker" is a planeswalker with the *subtype* Tezzeret, so any
 * Tezzeret card (or a permanent that has become one) satisfies it, not just the M19 planeswalker
 * deck's own Tezzeret.
 */
val TezzeretsStrider = card("Tezzeret's Strider") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Golem"
    power = 3
    toughness = 1
    oracleText = "As long as you control a Tezzeret planeswalker, this creature has menace. (It can't be blocked except by two or more creatures.)"

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.MENACE, GroupFilter.source()),
            condition = Conditions.YouControl(GameObjectFilter.Planeswalker.withSubtype("Tezzeret"))
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "290"
        artist = "Zack Stella"
        flavorText = "\"More obedient than any dog.\"\n" +
            "—Tezzeret"
        imageUri = "https://cards.scryfall.io/normal/front/8/c/8cf74535-31e3-4d2c-b42a-072580fa4ba0.jpg"
    }
}
