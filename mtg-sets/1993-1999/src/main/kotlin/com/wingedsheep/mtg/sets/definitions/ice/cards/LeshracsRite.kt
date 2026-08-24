package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Leshrac's Rite
 * {B}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature has swampwalk.
 *
 * The Cave Sense shape without the stat bump: one `GrantKeyword` on the default
 * `attachedCreature()` filter, plus the `auraTarget` enchant restriction. Granted landwalk is
 * engine-live — `BlockEvasionRules.LandwalkRule` reads the keyword out of projected state and maps
 * `SWAMPWALK` to the Swamp subtype — so the grant behaves exactly like a printed one.
 */
val LeshracsRite = card("Leshrac's Rite") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature has swampwalk. (It can't be blocked as long as defending player controls a Swamp.)"

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantKeyword(Keyword.SWAMPWALK)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "143"
        artist = "Richard Thomas"
        flavorText = "\"Bind me to thee, my soul to thine. I am your servant and your slave. I shall hunger for your word and thirst for your blessing. Blood for blood, flesh for flesh, Leshrac, my lord.\"\n—Lim-Dûl, the Necromancer"
        imageUri = "https://cards.scryfall.io/normal/front/4/e/4e0a6b4e-95b4-40f6-bb19-568dbd908a2b.jpg"
    }
}
