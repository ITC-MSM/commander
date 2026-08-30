package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dark Favor
 * {1}{B}
 * Enchantment — Aura
 * Enchant creature
 * When this Aura enters, you lose 1 life.
 * Enchanted creature gets +3/+1.
 *
 * The black counterpart of Divine Favor, and the same shape: an `auraTarget` attachment
 * restriction, an enters trigger, and a bare [ModifyStats] on the enchanted creature.
 *
 * Canonical printing: Magic 2012, the card's earliest real printing. Reprinted in Magic 2014.
 */
val DarkFavor = card("Dark Favor") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
            "When this Aura enters, you lose 1 life.\n" +
            "Enchanted creature gets +3/+1."

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.LoseLife(1, EffectTarget.Controller)
    }

    staticAbility {
        ability = ModifyStats(+3, +1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "89"
        artist = "Allen Williams"
        flavorText = "When he began to curse what he held holy, his strength grew unrivaled."
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a258a235-086e-429b-9ac1-3178f902658b.jpg"
    }
}
