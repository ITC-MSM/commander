package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Fire-Rim Form
 * {1}{R}
 * Enchantment — Aura
 * Flash
 * Enchant creature
 * When this Aura enters, enchanted creature gains first strike until end of turn.
 * Enchanted creature gets +2/+0.
 */
val FireRimForm = card("Fire-Rim Form") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Aura"
    oracleText = "Flash\n" +
        "Enchant creature\n" +
        "When this Aura enters, enchanted creature gains first strike until end of turn.\n" +
        "Enchanted creature gets +2/+0."

    keywords(Keyword.FLASH)

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GrantKeyword(Keyword.FIRST_STRIKE, EffectTarget.EnchantedCreature)
    }

    staticAbility {
        ability = ModifyStats(2, 0)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "107"
        artist = "Filipe Pagliuso"
        imageUri = "https://cards.scryfall.io/normal/front/3/2/32dc1bf4-a135-449f-848f-361a5360fae1.jpg?1743204392"
    }
}
