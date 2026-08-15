package com.wingedsheep.mtg.sets.definitions.ecl.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Dundoolin Weaver
 * {1}{G}
 * Creature — Kithkin Druid
 * 2/1
 *
 * When this creature enters, if you control three or more creatures, return target permanent
 * card from your graveyard to your hand.
 */
val DundoolinWeaver = card("Dundoolin Weaver") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Kithkin Druid"
    power = 2
    toughness = 1
    oracleText = "When this creature enters, if you control three or more creatures, return target permanent card from your graveyard to your hand."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.ControlCreaturesAtLeast(3)
        val permanentCard = target(
            "permanent card from your graveyard",
            TargetObject(filter = TargetFilter(GameObjectFilter.Permanent.ownedByYou(), zone = Zone.GRAVEYARD))
        )
        effect = Effects.ReturnToHand(permanentCard)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "175"
        artist = "Olivier Bernard"
        flavorText = "Stitching a mind back into the thoughtweft takes time, artistry, and the faith of an entire clachan."
        imageUri = "https://cards.scryfall.io/normal/front/2/2/2260912b-4dfb-49dd-bf95-060d44333645.jpg?1767862918"
    }
}
