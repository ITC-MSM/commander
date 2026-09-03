package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Aid the Fallen — War of the Spark #76 (canonical printing)
 * {1}{B}
 * Sorcery
 * Choose one or both —
 * • Return target creature card from your graveyard to your hand.
 * • Return target planeswalker card from your graveyard to your hand.
 *
 * "Choose one or both" is the modal *count*, not a third "do both" mode:
 * `chooseCount = 2` with `minChooseCount = 1`, the same spelling Scour for Scrap uses. An extra
 * "do both" mode would report one chosen mode where the spell in fact chose two.
 * Each mode carries its own target, both read out of your own graveyard —
 * [TargetFilter.CreatureInYourGraveyard] for the first, and `TargetFilter.Planeswalker` narrowed
 * with `ownedByYou().inZone(Zone.GRAVEYARD)` for the second, since the SDK has no pre-built
 * planeswalker-in-your-graveyard constant. Ownership, not control, is the axis for a card in a
 * graveyard.
 */
val AidTheFallen = card("Aid the Fallen") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Choose one or both —\n" +
        "• Return target creature card from your graveyard to your hand.\n" +
        "• Return target planeswalker card from your graveyard to your hand."

    spell {
        modal(chooseCount = 2, minChooseCount = 1) {
            mode("Return target creature card from your graveyard to your hand.") {
                val creature = target("target", Targets.CreatureCardInYourGraveyard)
                effect = Effects.ReturnToHand(creature)
            }
            mode("Return target planeswalker card from your graveyard to your hand.") {
                val planeswalker = target(
                    "target",
                    TargetObject(
                        filter = TargetFilter.Planeswalker.ownedByYou().inZone(Zone.GRAVEYARD)
                    )
                )
                effect = Effects.ReturnToHand(planeswalker)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "76"
        artist = "Sara Winters"
        flavorText = "\"I never liked you. Now get up—we have a fight to finish.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5f8bc010-f1af-42a2-9009-2039cf3d8f0a.jpg"
    }
}
