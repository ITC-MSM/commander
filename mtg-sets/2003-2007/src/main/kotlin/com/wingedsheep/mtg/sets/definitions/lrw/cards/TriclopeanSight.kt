package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Triclopean Sight
 * {1}{W}
 * Enchantment — Aura
 * Flash
 * Enchant creature
 * When this Aura enters, untap enchanted creature.
 * Enchanted creature gets +1/+1 and has vigilance.
 *
 * Flash plus the untap is the trick: cast it on a tapped attacker or blocker and it stands back up,
 * and the vigilance keeps it that way afterwards. The untap is a one-shot from the enters trigger,
 * so it fires once on arrival — [EffectTarget.EnchantedCreature] resolves through the Aura's own
 * attachment, which is already in place by the time the trigger resolves. The +1/+1 and the
 * vigilance are the continuous half and stay scoped to [Filters.EnchantedCreature].
 */
val TriclopeanSight = card("Triclopean Sight") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Flash\n" +
        "Enchant creature\n" +
        "When this Aura enters, untap enchanted creature.\n" +
        "Enchanted creature gets +1/+1 and has vigilance."

    keywords(Keyword.FLASH)

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Untap(EffectTarget.EnchantedCreature)
        description = "When this Aura enters, untap enchanted creature."
    }

    staticAbility {
        ability = ModifyStats(1, 1, Filters.EnchantedCreature)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.VIGILANCE, Filters.EnchantedCreature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "45"
        artist = "Scott Hampton"
        imageUri = "https://cards.scryfall.io/normal/front/4/4/444b5d72-ac5b-43d2-b5dc-0cc4bf63e43d.jpg?1783942908"
    }
}
