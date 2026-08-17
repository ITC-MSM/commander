package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetSpell
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Repulsive Mutation
 * {X}{G}{U}
 * Instant
 *
 * Put X +1/+1 counters on target creature you control. Then counter up to one target spell unless
 * its controller pays mana equal to the greatest power among creatures you control.
 */
val RepulsiveMutation = card("Repulsive Mutation") {
    manaCost = "{X}{G}{U}"
    colorIdentity = "GU"
    typeLine = "Instant"
    oracleText = "Put X +1/+1 counters on target creature you control. Then counter up to one target " +
        "spell unless its controller pays mana equal to the greatest power among creatures you control."

    spell {
        val creature = target("creature you control", Targets.CreatureYouControl)
        target("up to one target spell", TargetSpell(optional = true))
        effect = Effects.Composite(
            Effects.AddDynamicCounters(Counters.PLUS_ONE_PLUS_ONE, DynamicAmount.XValue, creature),
            Effects.CounterUnlessDynamicPays(
                DynamicAmounts.battlefield(Player.You, GameObjectFilter.Creature).maxPower()
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "227"
        artist = "Filip Burburan"
        flavorText = "\"Remind me to send the biomancer team a gift basket.\"\n" +
            "—Olana, Simic field researcher"
        imageUri =
            "https://cards.scryfall.io/normal/front/7/1/71701c28-f113-4d38-8fd3-a19cd9749661.jpg?1783912838"

        ruling(
            "2024-02-02",
            "If you control no creatures as Repulsive Mutation is resolving, the amount of mana the " +
                "target spell's controller must pay to stop their spell from being countered is 0. " +
                "That player can choose not to pay 0 mana; if they do, the spell will be countered."
        )
    }
}
