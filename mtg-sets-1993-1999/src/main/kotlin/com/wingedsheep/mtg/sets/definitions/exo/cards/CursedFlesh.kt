package com.wingedsheep.mtg.sets.definitions.exo.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Cursed Flesh
 * {B}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets -1/-1 and has fear.
 */
val CursedFlesh = card("Cursed Flesh") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\nEnchanted creature gets -1/-1 and has fear. " +
        "(It can't be blocked except by artifact creatures and/or black creatures.)"

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(-1, -1)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FEAR)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "56"
        artist = "Ron Spencer"
        flavorText = "A farewell to arms . . . and feet . . . and legs . . . ."
        imageUri = "https://cards.scryfall.io/normal/front/7/4/7433b9bf-ee6e-41fe-b826-0d20584198b1.jpg?1562087858"
    }
}
