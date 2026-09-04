package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Giltgrove Stalker
 * {1}{G}
 * Creature — Merfolk Warrior
 * 2/1
 * This creature can't be blocked by creatures with power 2 or less.
 */
val GiltgroveStalker = card("Giltgrove Stalker") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Merfolk Warrior"
    oracleText = "This creature can't be blocked by creatures with power 2 or less."
    power = 2
    toughness = 1

    staticAbility {
        ability = CantBeBlockedBy(GameObjectFilter.Creature.powerAtMost(2))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "131"
        artist = "Chris Seaman"
        flavorText = "\"The only gold I need is the sheltering shimmer of the trees.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/7/47963f87-d5c2-4e5b-8dff-b25735182841.jpg?1783935287"
        ruling(
            "2018-01-19",
            "Once a creature with power 3 or greater has blocked this creature, changing the power " +
                "of the blocking creature won't cause this creature to become unblocked."
        )
    }
}
