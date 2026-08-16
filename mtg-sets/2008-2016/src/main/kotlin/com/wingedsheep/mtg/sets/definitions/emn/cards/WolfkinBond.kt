package com.wingedsheep.mtg.sets.definitions.emn.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Wolfkin Bond
 * {4}{G}
 * Enchantment — Aura
 * Enchant creature
 * When this Aura enters, create a 2/2 green Wolf creature token.
 * Enchanted creature gets +2/+2.
 *
 * The Lunarch Mantle shape: [ModifyStats]`(2, 2)` with no filter is the Aura's static buff on its
 * attached creature, and the one-shot token is a plain self-ETB trigger, so the Wolf's controller is
 * the Aura's caster. The token's art comes from Eldritch Moon's synced token set, so no explicit
 * `imageUri` is needed.
 */
val WolfkinBond = card("Wolfkin Bond") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\nWhen this Aura enters, create a 2/2 green Wolf creature token.\nEnchanted creature gets +2/+2."

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Wolf"),
        )
        description = "Create a 2/2 green Wolf creature token."
    }

    staticAbility {
        ability = ModifyStats(2, 2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "178"
        artist = "Lindsey Look"
        imageUri = "https://cards.scryfall.io/normal/front/7/4/748f263e-efef-4033-8950-c58fd024a87f.jpg?1783937435"
        ruling(
            "2019-07-12",
            "You need a creature for Wolfkin Bond to target as you cast it. It can't enter the " +
                "battlefield attached to the Wolf token it will create."
        )
        ruling(
            "2019-07-12",
            "If the target creature is an illegal target by the time Wolfkin Bond tries to resolve, " +
                "the spell doesn't resolve. It won't enter the battlefield, so its ability won't trigger."
        )
    }
}
