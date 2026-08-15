package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ControlEnchantedPermanent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Tractor Beam
 * {2}{U}{U}
 * Enchantment — Aura
 *
 * Enchant creature or Spacecraft
 * When this Aura enters, tap enchanted permanent.
 * You control enchanted permanent.
 * Enchanted permanent doesn't untap during its controller's untap step.
 */
val TractorBeam = card("Tractor Beam") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature or Spacecraft\n" +
        "When this Aura enters, tap enchanted permanent.\n" +
        "You control enchanted permanent.\n" +
        "Enchanted permanent doesn't untap during its controller's untap step."

    auraTarget = TargetPermanent(
        filter = TargetFilter(
            GameObjectFilter.Creature.or(GameObjectFilter.Permanent.withSubtype("Spacecraft"))
        )
    )

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Tap(EffectTarget.EnchantedPermanent)
    }

    staticAbility {
        ability = ControlEnchantedPermanent
    }

    staticAbility {
        ability = GrantKeyword(AbilityFlag.DOESNT_UNTAP.name)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "82"
        artist = "Sergey Glushakov"
        imageUri = "https://cards.scryfall.io/normal/front/e/f/efc96d54-d6e1-49b6-b4c2-70b997776548.jpg?1752946884"
    }
}
