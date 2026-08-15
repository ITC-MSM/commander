package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Henchbots
 * {4}
 * Artifact Creature — Robot
 * 2/3
 *
 * When this creature enters, exile target tapped creature an opponent
 * controls until this creature leaves the battlefield.
 */
val Henchbots = card("Henchbots") {
    manaCost = "{4}"
    typeLine = "Artifact Creature — Robot"
    oracleText = "When this creature enters, exile target tapped creature an opponent controls until this creature leaves the battlefield."
    power = 2
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target(
            "tapped creature an opponent controls",
            TargetPermanent(filter = TargetFilter.Creature.opponentControls().tapped())
        )
        effect = Effects.ExileUntilLeaves(creature)
    }

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.ReturnLinkedExileUnderOwnersControl()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "174"
        artist = "Lordigan"
        flavorText = "\"Apologies for the disturbance, [INCIDENTAL WITNESS], have a pleasant day!\""
        imageUri = "https://cards.scryfall.io/normal/front/d/7/d77aa46e-fa57-4427-b0a3-0a957dc994dc.jpg?1771502803"
    }
}
