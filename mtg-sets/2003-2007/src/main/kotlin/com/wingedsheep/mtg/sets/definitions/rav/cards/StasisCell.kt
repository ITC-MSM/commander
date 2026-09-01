package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Stasis Cell
 * {4}{U}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature doesn't untap during its controller's untap step.
 * {3}{U}: Attach this Aura to target creature.
 *
 * The lock is the shared [AbilityFlag.DOESNT_UNTAP] grant onto the enchanted creature (Shackles'
 * shape) — `BeginningPhaseManager.performUntapStep` skips any permanent whose projected keywords
 * carry the flag, so the restriction follows the Aura when it moves rather than staying on the old
 * host.
 *
 * Moving the Aura is `Effects.AttachEquipment` aimed at the ability's own target: that effect is
 * attachment-generic (it rewrites `AttachedToComponent` / `AttachmentsComponent` and detaches from
 * the previous host first), not Equipment-specific — the same call Bound by Moonsilver makes. The
 * Aura stays under its controller's control while enchanting an opponent's creature, so only that
 * controller ever sees the activation. Unlike an Aura moved by "attach" during the enchant-target
 * check, the new host must merely be a creature; the printed ability names no further restriction.
 */
val StasisCell = card("Stasis Cell") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature doesn't untap during its controller's untap step.\n" +
        "{3}{U}: Attach this Aura to target creature."

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantKeyword(AbilityFlag.DOESNT_UNTAP.name)
    }

    activatedAbility {
        cost = Costs.Mana("{3}{U}")
        target = Targets.Creature
        effect = Effects.AttachEquipment(EffectTarget.ContextTarget(0))
        description = "{3}{U}: Attach this Aura to target creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "66"
        artist = "Mark A. Nelson"
        flavorText = "The Simic created the cells to preserve their experiments. " +
            "The Azorius put the cells to use on the guilty."
        imageUri = "https://cards.scryfall.io/normal/front/e/7/e75611d2-6e87-4032-a71c-b46806798a29.jpg?1783943680"
    }
}
