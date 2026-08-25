package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Vesperlark
 * {2}{W}
 * Creature — Elemental
 * 2/1
 * Flying
 * When this creature leaves the battlefield, return target creature card with power 1 or less from your graveyard to the battlefield.
 * Evoke {1}{W} (You may cast this spell for its evoke cost. If you do, it's sacrificed when it enters.)
 *
 * The trigger is a plain leaves-the-battlefield ([Triggers.LeavesBattlefield], SELF, any
 * destination) — evoke's sacrifice is one of the ways it fires, which is the card's whole point.
 */
val Vesperlark = card("Vesperlark") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Elemental"
    power = 2
    toughness = 1
    oracleText = "Flying\n" +
        "When this creature leaves the battlefield, return target creature card with power 1 or less from your graveyard to the battlefield.\n" +
        "Evoke {1}{W} (You may cast this spell for its evoke cost. If you do, it's sacrificed when it enters.)"

    keywords(Keyword.FLYING)

    evoke = "{1}{W}"

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        val t = target(
            "target",
            TargetObject(filter = TargetFilter.CreatureInYourGraveyard.powerAtMost(1))
        )
        effect = Effects.Move(t, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD)
        description = "When this creature leaves the battlefield, return target creature card with " +
            "power 1 or less from your graveyard to the battlefield."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "35"
        artist = "Raoul Vitale"
        imageUri = "https://cards.scryfall.io/normal/front/3/2/32776159-3fb6-4a70-be84-837ccd1d54a7.jpg?1783933151"
    }
}
