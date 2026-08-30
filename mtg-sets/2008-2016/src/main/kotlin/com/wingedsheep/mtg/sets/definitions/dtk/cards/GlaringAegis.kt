package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Glaring Aegis
 * {W}
 * Enchantment — Aura
 *
 * Enchant creature
 * When this Aura enters, tap target creature an opponent controls.
 * Enchanted creature gets +1/+3.
 *
 * Three separate lines, three separate pieces. "Enchant creature" is the attachment requirement
 * (`auraTarget`), which is chosen as the Aura *spell*'s target. The enters trigger is an ordinary
 * self ETB triggered ability with a target of its own — the creature it taps has nothing to do
 * with the creature it enchants. "Enchanted creature gets +1/+3" is a static ability whose
 * affected set is [ModifyStats]'s default, the attached permanent.
 */
val GlaringAegis = card("Glaring Aegis") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "When this Aura enters, tap target creature an opponent controls.\n" +
        "Enchanted creature gets +1/+3."

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", TargetCreature(filter = TargetFilter.CreatureOpponentControls))
        effect = Effects.Tap(t)
    }

    staticAbility {
        ability = ModifyStats(1, 3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "18"
        artist = "Anthony Palumbo"
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a5f66474-05bb-4235-b8b4-c2e6452118fc.jpg?1783938616"
    }
}
