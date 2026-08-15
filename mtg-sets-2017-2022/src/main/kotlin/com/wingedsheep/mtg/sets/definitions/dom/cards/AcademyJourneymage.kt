package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Academy Journeymage
 * {4}{U}
 * Creature — Human Wizard
 * 3/2
 * This spell costs {1} less to cast if you control a Wizard.
 * When Academy Journeymage enters the battlefield, return target creature
 * an opponent controls to its owner's hand.
 */
val AcademyJourneymage = card("Academy Journeymage") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 3
    toughness = 2
    oracleText = "This spell costs {1} less to cast if you control a Wizard.\nWhen Academy Journeymage enters the battlefield, return target creature an opponent controls to its owner's hand."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.FixedIfControlFilter(
                    amount = 1,
                    filter = GameObjectFilter.Any.withSubtype("Wizard"),
                ),
            ),
        )
    }

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("creature", Targets.CreatureOpponentControls)
        effect = Effects.ReturnToHand(creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "41"
        artist = "Magali Villeneuve"
        flavorText = "\"We don't choose who comes here. We choose how long they stay.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/4/a46a65e0-66a3-4896-8acc-0ad5e9927c40.jpg?1562740666"
    }
}
