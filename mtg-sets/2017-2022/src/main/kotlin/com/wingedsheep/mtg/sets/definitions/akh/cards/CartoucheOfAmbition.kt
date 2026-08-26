package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Cartouche of Ambition
 * {2}{B}
 * Enchantment — Aura Cartouche
 * Enchant creature you control
 * When this Aura enters, you may put a -1/-1 counter on target creature.
 * Enchanted creature gets +1/+1 and has lifelink.
 *
 * The printed "you may" is the resolution-time yes/no: `optional = true` lowers to the
 * `Gate.MayDecide` gate around the counter, so the target is still chosen on announcement.
 */
val CartoucheOfAmbition = card("Cartouche of Ambition") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Aura Cartouche"
    oracleText = "Enchant creature you control\n" +
        "When this Aura enters, you may put a -1/-1 counter on target creature.\n" +
        "Enchanted creature gets +1/+1 and has lifelink."

    auraTarget = Targets.CreatureYouControl

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", Targets.Creature)
        optional = true
        effect = Effects.AddCounters(Counters.MINUS_ONE_MINUS_ONE, 1, t)
    }

    staticAbility {
        ability = ModifyStats(1, 1)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.LIFELINK)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "83"
        artist = "Kieran Yanner"
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e68b96b1-b75e-4ee1-a6d7-6545a34fef9b.jpg?1783936514"
    }
}
