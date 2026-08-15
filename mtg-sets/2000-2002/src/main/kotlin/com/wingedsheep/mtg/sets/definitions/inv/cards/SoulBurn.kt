package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Soul Burn
 * {X}{2}{B}
 * Sorcery
 * Spend only black and/or red mana on X.
 * Soul Burn deals X damage to any target. You gain life equal to the amount of black
 * mana spent this way.
 *
 * The `xManaRestriction` limits the `{X}` portion to black/red mana, and
 * [DynamicAmount.ManaSpentOnX] reads how much of that was black to size the life gain.
 *
 * Note: this implements the original Invasion wording (life gained = black mana spent on
 * X). The current Oracle text additionally caps the life gain at the actual damage dealt /
 * the target's life total / loyalty / toughness; those secondary caps are omitted as they
 * only differ in edge cases where the target survives with fewer hit points than X.
 */
val SoulBurn = card("Soul Burn") {
    manaCost = "{X}{2}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Spend only black and/or red mana on X.\n" +
        "Soul Burn deals X damage to any target. You gain life equal to the amount of " +
        "black mana spent this way."

    spell {
        xManaRestriction = setOf(Color.BLACK, Color.RED)
        target = Targets.Any
        effect = Effects.Composite(
            Effects.DealXDamage(EffectTarget.ContextTarget(0)),
            Effects.GainLife(DynamicAmount.ManaSpentOnX(Color.BLACK))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "124"
        artist = "Andrew Goldhawk"
        imageUri = "https://cards.scryfall.io/normal/front/7/0/70515cd2-97d5-4491-a758-bc7188fdc6dc.jpg?1562917470"
    }
}
