package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Vanish into Eternity
 * {2}{W}
 * Instant
 *
 * This spell costs {3} more to cast if it targets a creature.
 * Exile target nonland permanent.
 *
 * The cost clause is Dragon's Prey's shape: a **top-level** [ModifySpellCost] on
 * [SpellCostTarget.SelfCast] whose condition lives inside the modification
 * ([CostModification.IncreaseGenericIfAnyTargetMatches]). Wrapping it in a conditional
 * static ability would hide it from cost calculation. The increase is locked in from the
 * target chosen at cast time (CR 601.2f).
 */
val VanishIntoEternity = card("Vanish into Eternity") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "This spell costs {3} more to cast if it targets a creature.\n" +
        "Exile target nonland permanent."

    spell {
        val permanent = target("target nonland permanent", Targets.NonlandPermanent)
        effect = Effects.Move(permanent, Zone.EXILE)
    }

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.IncreaseGenericIfAnyTargetMatches(
                amount = 3,
                filter = GameObjectFilter.Creature,
            ),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "36"
        artist = "Magali Villeneuve"
        imageUri = "https://cards.scryfall.io/normal/front/8/f/8f0b3308-9c0b-4461-9094-38deec20e1bc.jpg?1783918072"
    }
}
