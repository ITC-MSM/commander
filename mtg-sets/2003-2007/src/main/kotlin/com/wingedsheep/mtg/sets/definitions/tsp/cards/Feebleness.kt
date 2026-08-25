package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Feebleness
 * {1}{B}
 * Enchantment — Aura
 * Flash
 * Enchant creature
 * Enchanted creature gets -2/-1.
 *
 * Flash on a shrinking Aura makes it removal at instant speed: -2/-1 kills an X/1 outright and
 * blanks a combat trick. The penalty is the ordinary attached-creature [ModifyStats] static, whose
 * default filter is the enchanted creature.
 */
val Feebleness = card("Feebleness") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Aura"
    oracleText = "Flash\n" +
        "Enchant creature\n" +
        "Enchanted creature gets -2/-1."

    keywords(Keyword.FLASH)

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(-2, -1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "110"
        artist = "Kev Walker"
        flavorText = "Just a small touch of magic can harness the debilitating power of Urborg's poisonous winds."
        imageUri = "https://cards.scryfall.io/normal/front/1/b/1ba2660d-b661-4266-b7e5-07bb8b72bce6.jpg"
    }
}
