package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Field of Reality
 * {2}{U}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature can't be blocked by Spirits.
 * {1}{U}: Return this Aura to its owner's hand.
 *
 * "Enchant creature" is the `auraTarget` declaration — without it the Aura has nothing to attach
 * to. The evasion clause is a [CantBeBlockedBy] static scoped to [GroupFilter.attachedCreature],
 * so it follows the enchanted creature rather than the Aura itself. "Spirits" is a bare tribal
 * noun with no "creature" after it, so the blocker filter is `Permanent.withSubtype("Spirit")`
 * (blockers are creatures anyway; the printed word decides the filter). The self-bounce is the
 * ordinary [Effects.Move] of [EffectTarget.Self] to hand.
 */
val FieldOfReality = card("Field of Reality") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature can't be blocked by Spirits.\n" +
        "{1}{U}: Return this Aura to its owner's hand."

    auraTarget = Targets.Creature

    staticAbility {
        ability = CantBeBlockedBy(
            blockerFilter = GameObjectFilter.Permanent.withSubtype("Spirit"),
            filter = GroupFilter.attachedCreature()
        )
    }

    activatedAbility {
        cost = Costs.Mana("{1}{U}")
        effect = Effects.Move(EffectTarget.Self, Zone.HAND)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "60"
        artist = "Christopher Rush"
        flavorText = "\"The scholars of the Minamo School understood the veil between their world and that of the kami. Moreover, they knew how to exploit it.\"\n—*Observations of the Kami War*"
        imageUri = "https://cards.scryfall.io/normal/front/2/f/2ff7c6fc-324b-4cff-9109-6d817767865f.jpg?1783944328"
    }
}
