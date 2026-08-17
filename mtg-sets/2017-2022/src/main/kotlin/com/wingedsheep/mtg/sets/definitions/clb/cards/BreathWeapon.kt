package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Breath Weapon
 * {2}{R}
 * Instant
 * Breath Weapon deals 2 damage to each non-Dragon creature.
 *
 * A plain sweeper with a tribal hole in it: [Patterns.Group.dealDamageToAll] iterates a
 * [GroupFilter] whose base is [GameObjectFilter.Creature] narrowed by `notSubtype(DRAGON)`, so the
 * damage lands on every creature on the battlefield that isn't a Dragon — the printed word is
 * "creature", so the Dragon exclusion rides on the creature filter rather than a bare tribal one.
 */
val BreathWeapon = card("Breath Weapon") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Breath Weapon deals 2 damage to each non-Dragon creature."

    spell {
        effect = Patterns.Group.dealDamageToAll(
            amount = 2,
            filter = GroupFilter(GameObjectFilter.Creature.notSubtype(Subtype.DRAGON)),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "165"
        artist = "Adam Vehige"
        flavorText = "\"In the name of Tempus, Lord of Battles, you will die honorably in righteous fire.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/1/0174e40a-0ef5-4439-91e6-3fc39f482520.jpg?1783922743"
    }
}
