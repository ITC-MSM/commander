package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Grim Discovery
 * {1}{B}
 * Sorcery
 * Choose one or both —
 * • Return target creature card from your graveyard to your hand.
 * • Return target land card from your graveyard to your hand.
 *
 * "Choose one or both" is `modal(chooseCount = 2, minChooseCount = 1)` — `chooseCount` is the
 * ceiling and `minChooseCount` the floor (CR 700.2). Each mode carries its own target.
 */
val GrimDiscovery = card("Grim Discovery") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Choose one or both —\n" +
        "• Return target creature card from your graveyard to your hand.\n" +
        "• Return target land card from your graveyard to your hand."

    spell {
        modal(chooseCount = 2, minChooseCount = 1) {
            mode("Return target creature card from your graveyard to your hand") {
                val t = target("target", Targets.CreatureCardInYourGraveyard)
                effect = Effects.ReturnToHand(t)
            }
            mode("Return target land card from your graveyard to your hand") {
                val t = target(
                    "target",
                    TargetObject(filter = TargetFilter(GameObjectFilter.Land.ownedByYou(), zone = Zone.GRAVEYARD)),
                )
                effect = Effects.ReturnToHand(t)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "91"
        artist = "Christopher Moeller"
        flavorText = "Few among the living understand just how much of their world is shaped by the ruins of the dead."
        imageUri = "https://cards.scryfall.io/normal/front/4/e/4eeec3f1-1f12-4237-95ec-54bbc9a901f9.jpg"
    }
}
