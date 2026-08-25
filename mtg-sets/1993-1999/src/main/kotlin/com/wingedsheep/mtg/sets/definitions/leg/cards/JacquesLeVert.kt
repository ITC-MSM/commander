package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Jacques le Vert
 * {1}{R}{G}{W}
 * Legendary Creature — Human Warrior
 * 3/2
 *
 * Green creatures you control get +0/+2.
 */
val JacquesLeVert = card("Jacques le Vert") {
    manaCost = "{1}{R}{G}{W}"
    colorIdentity = "GRW"
    typeLine = "Legendary Creature — Human Warrior"
    power = 3
    toughness = 2
    oracleText = "Green creatures you control get +0/+2."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 0,
            toughnessBonus = 2,
            filter = GroupFilter(GameObjectFilter.Creature.withColor(Color.GREEN).youControl()),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "232"
        artist = "Andi Rusu"
        flavorText = "Abandoning his sword to return to the lush forest of Pendelhaven, Jacques le Vert devoted " +
            "his life to protecting the creatures of his homeland."
        imageUri = "https://cards.scryfall.io/normal/front/e/e/ee5a45b1-169b-468e-9251-424c09cd7f0f.jpg?1783948039"
    }
}
