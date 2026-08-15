package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Tarrian's Soulcleaver (LCI #264) — {1} Legendary Artifact — Equipment (rare)
 *
 * Equipped creature has vigilance.
 * Whenever another artifact or creature is put into a graveyard from the battlefield,
 * put a +1/+1 counter on equipped creature.
 * Equip {2}
 *
 * Implementation notes:
 * - Vigilance is a static [GrantKeyword] scoped to [Filters.EquippedCreature] (standard
 *   equipment keyword grant, à la Sword of Vengeance).
 * - The death payoff is a [Triggers.leavesBattlefield] trigger with `to = Zone.GRAVEYARD`
 *   over the union filter `Artifact or Creature` (any controller — no controller predicate),
 *   bound [TriggerBinding.OTHER] so the Soulcleaver itself (an artifact) never counts
 *   ("another"). The counter lands on [EffectTarget.EquippedCreature]; if the Equipment is
 *   unattached the trigger still fires but resolves as a no-op (no creature to grow).
 * - [equipAbility] wires the Equip {2} sorcery-speed attach.
 */
val TarriansSoulcleaver = card("Tarrian's Soulcleaver") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Legendary Artifact — Equipment"
    oracleText = "Equipped creature has vigilance.\n" +
        "Whenever another artifact or creature is put into a graveyard from the battlefield, " +
        "put a +1/+1 counter on equipped creature.\n" +
        "Equip {2}"

    staticAbility {
        ability = GrantKeyword(Keyword.VIGILANCE, Filters.EquippedCreature)
    }

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Artifact.or(GameObjectFilter.Creature),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.OTHER,
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.EquippedCreature)
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "264"
        artist = "Nereida"
        flavorText = "Tarrian's holy weapon still holds a fragment of his dark power... and his hunger."
        imageUri = "https://cards.scryfall.io/normal/front/9/9/99413817-1218-4947-b12f-9a952b095a89.jpg?1782694400"
    }
}
