package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Nature's Embrace
 * {2}{G}
 * Enchantment — Aura
 *
 * Enchant creature or land
 * As long as enchanted permanent is a creature, it gets +2/+2.
 * As long as enchanted permanent is a land, it has "{T}: Add two mana of any one color."
 *
 * A dual-mode Aura ([TargetFilter.CreatureOrLandPermanent] auraTarget). Both grants are statics
 * over the enchanted permanent ([GroupFilter.attachedCreature] = `Scope.AttachedTo`, the "enchanted
 * permanent" scope regardless of the host's type), each gated by an [Conditions.EnchantedPermanentMatches]
 * check on the host's *current* type so the buff and the mana ability switch on continuously if the
 * host ever changes types. The granted land ability mirrors New Horizons' `{T}: Add two mana of any
 * one color` ([Effects.AddAnyColorMana]`(2)`).
 */
val NaturesEmbrace = card("Nature's Embrace") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature or land\n" +
        "As long as enchanted permanent is a creature, it gets +2/+2.\n" +
        "As long as enchanted permanent is a land, it has \"{T}: Add two mana of any one color.\""

    auraTarget = TargetPermanent(filter = TargetFilter.CreatureOrLandPermanent)

    // As long as enchanted permanent is a creature, it gets +2/+2.
    staticAbility {
        condition = Conditions.EnchantedPermanentMatches(GameObjectFilter.Creature)
        ability = ModifyStats(2, 2, GroupFilter.attachedCreature())
    }

    // As long as enchanted permanent is a land, it has "{T}: Add two mana of any one color."
    staticAbility {
        condition = Conditions.EnchantedPermanentMatches(GameObjectFilter.Land)
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Tap,
                effect = Effects.AddAnyColorMana(2)
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "211"
        artist = "Dominik Mayer"
        imageUri = "https://cards.scryfall.io/normal/front/3/9/39d757af-86fd-4f99-a09a-0f3898ed95f6.jpg?1783924808"
    }
}
