package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.costs.CostAtom
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Betrayal of Flesh
 * {5}{B}
 * Instant
 * Choose one —
 * • Destroy target creature.
 * • Return target creature card from your graveyard to the battlefield.
 * Entwine—Sacrifice three lands. (Choose both if you pay the entwine cost.)
 */
val BetrayalOfFlesh = card("Betrayal of Flesh") {
    manaCost = "{5}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Destroy target creature.\n" +
        "• Return target creature card from your graveyard to the battlefield.\n" +
        "Entwine—Sacrifice three lands. (Choose both if you pay the entwine cost.)"

    spell {
        modal(
            chooseCount = 2,
            minChooseCount = 1,
            additionalCostPerExtraMode = CostAtom.Sacrifice(GameObjectFilter.Land, count = 3),
        ) {
            mode("Destroy target creature") {
                val creature = target("creature to destroy", TargetCreature())
                effect = Effects.Destroy(creature)
            }
            mode("Return target creature card from your graveyard to the battlefield") {
                val card = target(
                    "creature card to return",
                    TargetObject(filter = TargetFilter.CreatureInYourGraveyard),
                )
                effect = Effects.Move(card, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD)
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "58"
        artist = "Wayne England"
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a9e2e107-0277-4e5c-81a7-258bb2998f3e.jpg?1783944549"
    }
}
