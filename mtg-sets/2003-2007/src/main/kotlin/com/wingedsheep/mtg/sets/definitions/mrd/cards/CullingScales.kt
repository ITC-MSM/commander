package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Culling Scales
 * {3}
 * Artifact
 *
 * At the beginning of your upkeep, destroy target nonland permanent with the lowest mana value.
 * (If two or more permanents are tied for lowest, target any one of them.)
 */
val CullingScales = card("Culling Scales") {
    manaCost = "{3}"
    typeLine = "Artifact"
    oracleText = "At the beginning of your upkeep, destroy target nonland permanent with the " +
        "lowest mana value. (If two or more permanents are tied for lowest, target any one of them.)"

    val abilityText = oracleText
    val nonlandPermanents = GameObjectFilter.NonlandPermanent
    triggeredAbility {
        trigger = Triggers.YourUpkeep
        target = TargetObject(
            filter = TargetFilter(nonlandPermanents.hasLeastManaValueAmong(nonlandPermanents))
        )
        effect = Effects.Destroy(EffectTarget.ContextTarget(0))
        description = abilityText
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "160"
        artist = "Daren Bader"
        imageUri = "https://cards.scryfall.io/normal/front/d/b/db9a57d6-bba6-463d-ad12-6f93c035d16b.jpg?1783944524"
        ruling("2004-12-01", "You choose the target. If there's more than one nonland permanent tied for lowest mana value, you choose which one to target.")
        ruling("2004-12-01", "If the targeted permanent doesn't have the lowest mana value when the ability resolves, the ability doesn't resolve and the permanent isn't destroyed.")
        ruling("2004-12-01", "Most tokens have a mana value of 0. A token that's a copy of another permanent or card has a mana value equal to that permanent or card's mana value.")
        ruling("2004-12-01", "If the lowest mana value is 3, Culling Scales can destroy itself.")
    }
}
