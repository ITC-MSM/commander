package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Ramosian Greatsword
 * {4}{R}
 * Artifact — Equipment
 * Convoke
 * Equipped creature gets +3/+1 and has trample.
 * Equip {2}
 */
val RamosianGreatsword = card("Ramosian Greatsword") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Artifact — Equipment"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while " +
        "casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Equipped creature gets +3/+1 and has trample.\n" +
        "Equip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)"

    keywords(Keyword.CONVOKE)

    staticAbility {
        ability = ModifyStats(3, 1, Filters.EquippedCreature)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.TRAMPLE, Filters.EquippedCreature)
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "159"
        artist = "Jason A. Engle"
        imageUri = "https://cards.scryfall.io/normal/front/e/f/efd6f04e-e83d-4580-ae3f-583ada848868.jpg?1783916985"
        ruling(
            "2024-01-12",
            "When calculating a spell's total cost, include any alternative costs, additional " +
                "costs, or anything else that increases or reduces the cost to cast the spell. " +
                "Convoke applies after the total cost is calculated. Convoke doesn't change a " +
                "spell's mana cost or mana value."
        )
    }
}
