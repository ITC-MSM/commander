package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Archaeomender
 * {2}{U}
 * Creature — Human Wizard
 * 2/3
 *
 * When this creature enters, return target artifact card from your graveyard to your hand.
 */
val Archaeomender = card("Archaeomender") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    oracleText = "When this creature enters, return target artifact card from your graveyard to your hand."
    power = 2
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val card = target(
            "target artifact card in your graveyard",
            TargetObject(filter = TargetFilter(GameObjectFilter.Artifact.ownedByYou(), zone = Zone.GRAVEYARD)),
        )
        effect = Effects.ReturnToHand(card)
        description = "When this creature enters, return target artifact card from your graveyard to your hand."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "9"
        artist = "Miranda Meeks"
        flavorText = "\"Just as a broken relic can be mended, a shattered history can be made whole.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/2/22bb8779-ac19-43d6-b818-86eb8ee2f87d.jpg?1783930507"
    }
}
