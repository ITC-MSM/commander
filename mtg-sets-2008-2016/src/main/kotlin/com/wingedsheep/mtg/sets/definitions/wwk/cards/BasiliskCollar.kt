package com.wingedsheep.mtg.sets.definitions.wwk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Basilisk Collar
 * {1}
 * Artifact — Equipment
 *
 * Equipped creature has deathtouch and lifelink.
 * Equip {2}
 */
val BasiliskCollar = card("Basilisk Collar") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature has deathtouch and lifelink. (Any amount of damage it deals to a creature is enough to destroy it. Damage dealt by this creature also causes you to gain that much life.)\nEquip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = GrantKeyword(Keyword.DEATHTOUCH, Filters.EquippedCreature)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.LIFELINK, Filters.EquippedCreature)
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "122"
        artist = "Howard Lyon"
        flavorText = "During their endless travels, the mages of the Goma Fada caravan have learned ways to harness both life and death."
        imageUri = "https://cards.scryfall.io/normal/front/5/5/55cdba1b-7a80-435f-9cff-b9365f62e311.jpg?1562287734"
    }
}
