package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Fire Nation Engineer
 * {2}{B}
 * Creature — Human Artificer
 * 2/3
 *
 * Raid — At the beginning of your end step, if you attacked this turn, put a +1/+1 counter
 * on another target creature or Vehicle you control.
 *
 * Raid is modeled as an intervening-if on a [Triggers.YourEndStep] triggered ability via
 * [Conditions.YouAttackedThisTurn] (checked both when the trigger would go on the stack and again
 * on resolution, CR 603.4). "Another target creature or Vehicle you control" is a
 * [GameObjectFilter.CreatureOrVehicle] permanent target restricted to your control with
 * `excludeSelf` ("another"); a Vehicle is matched by its subtype even while it isn't a creature.
 */
val FireNationEngineer = card("Fire Nation Engineer") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Artificer"
    power = 2
    toughness = 3
    oracleText = "Raid — At the beginning of your end step, if you attacked this turn, put a " +
        "+1/+1 counter on another target creature or Vehicle you control."

    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.YouAttackedThisTurn
        val target = target(
            "another target creature or Vehicle you control",
            TargetPermanent(
                filter = TargetFilter(
                    GameObjectFilter.CreatureOrVehicle.youControl(),
                    excludeSelf = true
                )
            )
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, target)
        description = "Raid — At the beginning of your end step, if you attacked this turn, put a " +
            "+1/+1 counter on another target creature or Vehicle you control."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "99"
        artist = "Norikatsu Miyoshi"
        flavorText = "A lonely worker can find trouble making friends on an airship so massive."
        imageUri = "https://cards.scryfall.io/normal/front/5/d/5daba108-277d-46c8-ac35-7610f4786813.jpg?1764120684"
    }
}
