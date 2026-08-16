package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Sandstone Bridge
 * Land
 * This land enters tapped.
 * When this land enters, target creature gets +1/+1 and gains vigilance until end of turn.
 * {T}: Add {W}.
 */
val SandstoneBridge = card("Sandstone Bridge") {
    manaCost = ""
    colorIdentity = "W"
    typeLine = "Land"
    oracleText = "This land enters tapped.\nWhen this land enters, target creature gets +1/+1 and gains vigilance until end of turn.\n{T}: Add {W}."

    replacementEffect(EntersTapped())

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(power = 1, toughness = 1, target = t),
            Effects.GrantKeyword(Keyword.VIGILANCE, target = t)
        )
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "243"
        artist = "Cliff Childs"
        imageUri = "https://cards.scryfall.io/normal/front/c/7/c781e932-4605-47aa-add1-4ee62f4e7ead.jpg?1783938173"
    }
}
