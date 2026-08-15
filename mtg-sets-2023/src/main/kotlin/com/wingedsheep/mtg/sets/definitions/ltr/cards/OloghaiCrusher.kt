package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBlockUnless
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Olog-hai Crusher
 * {3}{R}
 * Creature — Troll Soldier
 * 4/4
 *
 * Trample
 * This creature can't block unless you control a Goblin or Orc.
 */
val OloghaiCrusher = card("Olog-hai Crusher") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Troll Soldier"
    power = 4
    toughness = 4
    oracleText = "Trample\nThis creature can't block unless you control a Goblin or Orc."

    keywords(Keyword.TRAMPLE)

    staticAbility {
        ability = CantBlockUnless(
            Exists(
                player = Player.You,
                zone = Zone.BATTLEFIELD,
                filter = GameObjectFilter.Creature.withAnySubtype("Goblin", "Orc")
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "140"
        artist = "Andrea Piparo"
        flavorText = "Trolls were abroad, no longer dull-witted, but cunning and armed with dreadful weapons."
        imageUri = "https://cards.scryfall.io/normal/front/c/3/c33bf593-62e0-491a-a31a-328bce6d8735.jpg?1686969084"
    }
}
