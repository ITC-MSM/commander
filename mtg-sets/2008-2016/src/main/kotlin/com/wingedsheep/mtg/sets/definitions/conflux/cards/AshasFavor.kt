package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Asha's Favor
 * {2}{W}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature has flying, first strike, and vigilance.
 *
 * "Enchant creature" is the `auraTarget`, without which the Aura would have nothing to attach to
 * and would never enter attached. The three granted keywords are three [GrantKeyword] statics,
 * each left on its default `attachedCreature()` filter so it reads "enchanted creature" — the SDK
 * models one grant per keyword rather than a combined set.
 */
val AshasFavor = card("Asha's Favor") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature has flying, first strike, and vigilance."

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FIRST_STRIKE)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.VIGILANCE)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "2"
        artist = "Donato Giancola"
        flavorText = "As his new wings lifted him high above Bant, Taric felt his earthly aspirations transform into heavenly resolve."
        imageUri = "https://cards.scryfall.io/normal/front/2/8/28b817f7-ae05-4d31-8a2c-29d8082b4132.jpg"
    }
}
