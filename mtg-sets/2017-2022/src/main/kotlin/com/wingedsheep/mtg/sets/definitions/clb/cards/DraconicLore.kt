package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostGating
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Draconic Lore
 * {5}{U}
 * Instant
 * This spell costs {2} less to cast if you control a Dragon.
 * Draw three cards.
 *
 * A plain [Effects.DrawCards] spell wearing a cost reducer: [ModifySpellCost] over
 * [SpellCostTarget.SelfCast] is the primitive the whole card hangs on, and its
 * [CostGating.OnlyIf] takes [Conditions.ControlPermanentOfType] because the bare tribal
 * noun "a Dragon" asks about any permanent with the subtype, not only a creature.
 */
val DraconicLore = card("Draconic Lore") {
    manaCost = "{5}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "This spell costs {2} less to cast if you control a Dragon.\n" +
        "Draw three cards."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGeneric(2),
            gating = CostGating.OnlyIf(Conditions.ControlPermanentOfType(Subtype.DRAGON)),
        )
    }

    spell {
        effect = Effects.DrawCards(3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "64"
        artist = "Tom Babbey"
        flavorText = "The wyrmling studied the ancient carvings and dreamed of a day when her own exploits would be immortalized in stone."
        imageUri = "https://cards.scryfall.io/normal/front/5/5/557f2aa6-0b82-4f60-9617-610b613c2a48.jpg?1783922792"
    }
}
