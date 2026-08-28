package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Chorus of Woe
 * {B}
 * Sorcery
 * Creatures you control get +1/+0 until end of turn.
 *
 * A group pump over the creatures you control — the same shape as Festergloom, with the
 * controller predicate on the group filter rather than the colour one.
 */
val ChorusOfWoe = card("Chorus of Woe") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Creatures you control get +1/+0 until end of turn."

    spell {
        effect = Patterns.Group.modifyStatsForAll(
            1, 0,
            GroupFilter(GameObjectFilter.Creature.youControl())
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "65"
        artist = "Randy Gallegos"
        flavorText = "When nightstalkers sing, nothing in creation sleeps."
        imageUri = "https://cards.scryfall.io/normal/front/0/2/0289ddb3-b35c-4edf-92bb-06f84a3475d9.jpg"
    }
}
