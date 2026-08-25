package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Survivors' Bond
 * {1}{G}
 * Sorcery
 *
 * Choose one or both —
 * • Return target Human creature card from your graveyard to your hand.
 * • Return target non-Human creature card from your graveyard to your hand.
 *
 * "Choose one or both" is `modal(chooseCount = 2, minChooseCount = 1)` — `chooseCount` is the
 * ceiling and `minChooseCount` the floor (CR 700.2). The two modes are deliberately disjoint:
 * `Human` vs `non-Human` is `HasSubtype` against `NotSubtype` on the same creature-card filter, so
 * taking both modes always recurs two different cards.
 *
 * Ownership, not control, is the axis on both filters: a card in a graveyard is neither a permanent
 * nor a spell, so it has no controller — only the owner whose graveyard it sits in.
 */
val SurvivorsBond = card("Survivors' Bond") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Choose one or both —\n" +
        "• Return target Human creature card from your graveyard to your hand.\n" +
        "• Return target non-Human creature card from your graveyard to your hand."

    spell {
        modal(chooseCount = 2, minChooseCount = 1) {
            mode("Return target Human creature card from your graveyard to your hand") {
                val t = target(
                    "target",
                    TargetObject(
                        filter = TargetFilter(
                            GameObjectFilter.Creature.withSubtype(Subtype.HUMAN).ownedByYou(),
                            zone = Zone.GRAVEYARD,
                        ),
                    ),
                )
                effect = Effects.ReturnToHand(t)
            }
            mode("Return target non-Human creature card from your graveyard to your hand") {
                val t = target(
                    "target",
                    TargetObject(
                        filter = TargetFilter(
                            GameObjectFilter.Creature.notSubtype(Subtype.HUMAN).ownedByYou(),
                            zone = Zone.GRAVEYARD,
                        ),
                    ),
                )
                effect = Effects.ReturnToHand(t)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "172"
        artist = "Randy Vargas"
        flavorText = "\"And what did we learn? Never taunt a porcuparrot!\""
        imageUri = "https://cards.scryfall.io/normal/front/5/3/530ff2a1-6447-4653-a661-d9a39156d6fa.jpg"
    }
}
