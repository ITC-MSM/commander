package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.LoseAllAbilities
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Frozen in Ice
 * {2}{U}
 * Enchantment — Aura
 *
 * Enchant creature
 * When this Aura enters, tap enchanted creature.
 * Enchanted creature loses all abilities and can't become untapped.
 *
 * The Stop Cold / Blossombind idiom: an ETB [Effects.Tap] on the enchanted creature plus a Layer 6
 * [LoseAllAbilities] and an untap restriction. The printed wording is the *stronger*
 * [AbilityFlag.CANT_BECOME_UNTAPPED] (as on Blossombind), which blocks explicit untap effects and
 * untap costs too — not just the controller's untap step, which is all `DOESNT_UNTAP` (Stop Cold)
 * covers.
 */
val FrozenInIce = card("Frozen in Ice") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "When this Aura enters, tap enchanted creature.\n" +
        "Enchanted creature loses all abilities and can't become untapped."

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Tap(EffectTarget.EnchantedCreature)
    }

    staticAbility {
        ability = LoseAllAbilities()
    }

    staticAbility {
        ability = GrantKeyword(AbilityFlag.CANT_BECOME_UNTAPPED.name)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "54"
        artist = "Ioannis Fiore"
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a990260b-39b7-4799-930a-bf9ac208d9ed.jpg?1783902959"
    }
}
