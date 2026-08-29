package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlocked
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Protective Bubble
 * {3}{U}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature can't be blocked and has shroud.
 */
val ProtectiveBubble = card("Protective Bubble") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\nEnchanted creature can't be blocked and has shroud. (It can't be the target " +
        "of spells or abilities.)"
    auraTarget = Targets.Creature

    staticAbility {
        // The evasion belongs to the enchanted creature, not this Aura — scope it to the attachment.
        ability = CantBeBlocked(GroupFilter.attachedCreature())
    }
    staticAbility {
        ability = GrantKeyword(Keyword.SHROUD)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "80"
        artist = "Steve Ellis"
        flavorText = "Skilled merrow rudders ensure their charges arrive on time and without incident."
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7ab1db58-66b6-4b92-9dcd-e044fa383469.jpg?1783942900"
    }
}
