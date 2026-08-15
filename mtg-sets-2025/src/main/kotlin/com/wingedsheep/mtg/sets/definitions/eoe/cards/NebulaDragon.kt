package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nebula Dragon
 * {6}{R}
 * Creature — Dragon
 * 4/4
 *
 * Flying
 * When this creature enters, it deals 3 damage to any target.
 */
val NebulaDragon = card("Nebula Dragon") {
    manaCost = "{6}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dragon"
    oracleText = "Flying\nWhen this creature enters, it deals 3 damage to any target."
    power = 4
    toughness = 4

    keywords(Keyword.FLYING)

    // ETB: deals 3 damage to any target
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val target = target("any target", Targets.Any)
        effect = Effects.DealDamage(3, target)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "147"
        artist = "Greg Staples"
        flavorText = "Legend says the first Regent Maximum was herself a dragon. She illuminated the void and gifted the Celestial Palatinate lightforging."
        imageUri = "https://cards.scryfall.io/normal/front/0/e/0ee509dd-9fba-4b6d-a9d4-cc8bf5822ddd.jpg?1752947147"
    }
}
