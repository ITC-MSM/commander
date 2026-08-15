package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.jobSelect
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantSubtype
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Warrior's Sword
 * {3}{R}
 * Artifact — Equipment
 * Job select (When this Equipment enters, create a 1/1 colorless Hero creature token,
 *   then attach this to it.)
 * Equipped creature gets +3/+2 and is a Warrior in addition to its other types.
 * Equip {5}
 */
val WarriorsSword = card("Warrior's Sword") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Artifact — Equipment"
    oracleText = "Job select (When this Equipment enters, create a 1/1 colorless Hero creature token, then attach this to it.)\n" +
        "Equipped creature gets +3/+2 and is a Warrior in addition to its other types.\n" +
        "Equip {5} ({5}: Attach to target creature you control. Equip only as a sorcery.)"

    jobSelect()

    staticAbility {
        ability = ModifyStats(3, 2, Filters.EquippedCreature)
    }
    staticAbility {
        ability = GrantSubtype("Warrior", Filters.EquippedCreature)
    }

    equipAbility("{5}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "169"
        artist = "Andrea Tentori Montalto"
        imageUri = "https://cards.scryfall.io/normal/front/c/b/cb98a7dd-542e-4448-b3bb-ff5d67a36535.jpg?1748706394"
    }
}
