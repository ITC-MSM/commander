package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Extravagant Replication
 * {4}{U}
 * Enchantment
 *
 * At the beginning of your upkeep, create a token that's a copy of another target nonland
 * permanent you control.
 *
 * "Another" excludes Extravagant Replication itself (it is a nonland permanent you control);
 * `TargetFilter.OtherNonlandPermanent.youControl()` restricts to your side and excludes the
 * source. The copy uses the target's *copiable* characteristics (Rule 707.2), so it carries
 * over printed abilities but not counters or one-shot effects.
 */
val ExtravagantReplication = card("Extravagant Replication") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "At the beginning of your upkeep, create a token that's a copy of another target nonland permanent you control."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        val copyTarget = target(
            "another target nonland permanent you control",
            TargetObject(filter = TargetFilter.OtherNonlandPermanent.youControl())
        )
        effect = Effects.CreateTokenCopyOfTarget(copyTarget)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "25"
        artist = "Pauline Voss"
        flavorText = "\"Just one aether tiger? What do I look like, a peasant?\""
        imageUri = "https://cards.scryfall.io/normal/front/6/a/6a6f55d7-d689-43eb-a59a-b8be88269ee6.jpg?1783923371"
    }
}
