package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.Effect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Overload
 * {R}
 * Instant
 * Kicker {2}
 * Destroy target artifact if its mana value is 2 or less. If this spell was kicked,
 * destroy that artifact if its mana value is 5 or less instead.
 */
private fun destroyIfManaValueAtMost(max: Int): Effect =
    ConditionalEffect(
        condition = Compare(
            left = DynamicAmount.EntityProperty(EntityReference.Target(0), EntityNumericProperty.ManaValue),
            operator = ComparisonOperator.LTE,
            right = DynamicAmount.Fixed(max)
        ),
        effect = Effects.Destroy(EffectTarget.ContextTarget(0))
    )

val Overload = card("Overload") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Kicker {2} (You may pay an additional {2} as you cast this spell.)\n" +
        "Destroy target artifact if its mana value is 2 or less. If this spell was kicked, " +
        "destroy that artifact if its mana value is 5 or less instead."

    keywordAbility(KeywordAbility.kicker("{2}"))

    spell {
        target = Targets.Artifact
        effect = ConditionalEffect(
            condition = WasKicked,
            effect = destroyIfManaValueAtMost(5),
            elseEffect = destroyIfManaValueAtMost(2)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "157"
        artist = "Gary Ruddell"
        imageUri = "https://cards.scryfall.io/normal/front/c/9/c91fca91-7296-422e-b251-d571b710ff71.jpg?1562935385"
    }
}
