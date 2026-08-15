package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedByMoreThan
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Meltstrider's Resolve
 * {G}
 * Enchantment — Aura
 *
 * Enchant creature you control
 * When this Aura enters, enchanted creature fights up to one target creature an opponent controls. (Each deals damage equal to its power to the other.)
 * Enchanted creature gets +0/+2 and can't be blocked by more than one creature.
 */
val MeltstridersResolve = card("Meltstrider's Resolve") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature you control\nWhen this Aura enters, enchanted creature fights up to one target creature an opponent controls. (Each deals damage equal to its power to the other.)\nEnchanted creature gets +0/+2 and can't be blocked by more than one creature."

    auraTarget = Targets.CreatureYouControl

    // ETB: enchanted creature fights up to one target creature an opponent controls
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val opponentCreature = target(
            "creature an opponent controls",
            TargetCreature(optional = true, filter = TargetFilter.CreatureOpponentControls)
        )
        effect = Effects.Fight(EffectTarget.EnchantedCreature, opponentCreature)
    }

    // Enchanted creature gets +0/+2 and can't be blocked by more than one creature
    staticAbility {
        ability = ModifyStats(0, 2)
    }

    staticAbility {
        ability = CantBeBlockedByMoreThan(maxBlockers = 1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "199"
        artist = "Carlos Palma Cruchaga"
        imageUri = "https://cards.scryfall.io/normal/front/5/3/53c08af4-b975-4d9d-baba-73e6727f2778.jpg?1752947367"
    }
}
