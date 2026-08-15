package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.GrantSupertype
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * On Serra's Wings
 * {3}{W}
 * Legendary Enchantment — Aura
 * Enchant creature
 * Enchanted creature is legendary, gets +1/+1, and has flying, vigilance, and lifelink.
 */
val OnSerrasWings = card("On Serra's Wings") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Enchantment — Aura"
    oracleText = "Enchant creature\nEnchanted creature is legendary, gets +1/+1, and has flying, vigilance, and lifelink."

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantSupertype("LEGENDARY")
    }

    staticAbility {
        ability = ModifyStats(1, 1)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.VIGILANCE)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.LIFELINK)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "28"
        artist = "Johannes Voss"
        flavorText = "The spirit of Serra raised Brindri high and commanded her to keep the balance."
        imageUri = "https://cards.scryfall.io/normal/front/0/6/063b8d2c-862b-458e-8f7c-c3e29a98c234.jpg?1562730873"
    }
}
