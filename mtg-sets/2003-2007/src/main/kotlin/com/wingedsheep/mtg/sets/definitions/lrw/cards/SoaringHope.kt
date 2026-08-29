package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Soaring Hope
 * {4}{W}
 * Enchantment — Aura
 * Enchant creature
 * When this Aura enters, you gain 3 life.
 * Enchanted creature has flying.
 * {W}: Put this Aura on top of its owner's library.
 */
val SoaringHope = card("Soaring Hope") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\nWhen this Aura enters, you gain 3 life.\n" +
        "Enchanted creature has flying.\n{W}: Put this Aura on top of its owner's library."

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(3)
        description = "you gain 3 life."
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING)
    }

    activatedAbility {
        cost = Costs.Mana("{W}")
        effect = Effects.PutOnTopOfLibrary(EffectTarget.Self)
        description = "{W}: Put this Aura on top of its owner's library."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "40"
        artist = "Martina Pilcerova"
        imageUri = "https://cards.scryfall.io/normal/front/7/f/7f6c24b1-af9d-4a9c-9167-faa8a397a361.jpg?1783942908"
    }
}
