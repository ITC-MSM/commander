package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Valor in Akros
 * {3}{W}
 * Enchantment
 * Whenever a creature you control enters, creatures you control get +1/+1 until end of turn.
 *
 * A plain "another permanent entered" trigger ([Triggers.entersBattlefield] over
 * `Creature.youControl()` with [TriggerBinding.ANY], since the watcher is the enchantment rather
 * than the entering creature) feeding [Patterns.Group.modifyStatsForAll] over
 * [GroupFilter.AllCreaturesYouControl]. The group is snapshotted when the ability resolves, so the
 * creature that caused the trigger is included and later arrivals are not (printed ruling).
 */
val ValorInAkros = card("Valor in Akros") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Whenever a creature you control enters, creatures you control get +1/+1 until end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.youControl(),
            binding = TriggerBinding.ANY
        )
        effect = Patterns.Group.modifyStatsForAll(1, 1, GroupFilter.AllCreaturesYouControl)
        description = "Creatures you control get +1/+1 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "39"
        artist = "Igor Kieryluk"
        flavorText = "They became a single entity, a phalanx in the Temple of Triumph standing against a host of enemies."
        imageUri = "https://cards.scryfall.io/normal/front/2/2/22fa0acd-84c3-492e-adf7-e7438db47e0a.jpg?1783938356"
        ruling(
            "2020-08-07",
            "Valor in Akros's ability affects only creatures you control at the time the ability " +
                "resolves, including the creature that caused it to trigger. Creatures you begin " +
                "to control later in the turn won't get +1/+1."
        )
    }
}
