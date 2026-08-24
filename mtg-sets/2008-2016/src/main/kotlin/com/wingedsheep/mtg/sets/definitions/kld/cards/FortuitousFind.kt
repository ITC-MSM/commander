package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Fortuitous Find
 * {2}{B}
 * Sorcery
 *
 * Choose one or both —
 * • Return target artifact card from your graveyard to your hand.
 * • Return target creature card from your graveyard to your hand.
 *
 * "Choose one or both" is `modal(chooseCount = 2, minChooseCount = 1)` — `chooseCount` is the
 * ceiling and `minChooseCount` the floor (CR 700.2). Each mode carries its own target, so an
 * artifact creature card can satisfy either bullet but not both with the same card: the two
 * requirements are chosen at the same time and must name different objects.
 */
val FortuitousFind = card("Fortuitous Find") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Choose one or both —\n" +
        "• Return target artifact card from your graveyard to your hand.\n" +
        "• Return target creature card from your graveyard to your hand."

    spell {
        modal(chooseCount = 2, minChooseCount = 1) {
            mode("Return target artifact card from your graveyard to your hand") {
                val t = target(
                    "target",
                    TargetObject(filter = TargetFilter.ArtifactInYourGraveyard),
                )
                effect = Effects.ReturnToHand(t)
            }
            mode("Return target creature card from your graveyard to your hand") {
                val t = target("target", Targets.CreatureCardInYourGraveyard)
                effect = Effects.ReturnToHand(t)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "81"
        artist = "Tomasz Jedruszek"
        flavorText = "An aetherborn wastes neither time nor treasure."
        imageUri = "https://cards.scryfall.io/normal/front/7/7/7706bbb1-c94a-4169-9f12-a54cfcc3a7ad.jpg?1783937209"
    }
}
