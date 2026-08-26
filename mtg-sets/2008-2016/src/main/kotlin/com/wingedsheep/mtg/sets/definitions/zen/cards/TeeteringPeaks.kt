package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Teetering Peaks
 * Land
 * This land enters tapped.
 * When this land enters, target creature gets +2/+0 until end of turn.
 * {T}: Add {R}.
 *
 * The Zendikar "enters tapped, then a one-shot rider on a creature" land cycle — the trigger
 * targets, so the land is a Falter/pump effect stapled to a land drop.
 */
val TeeteringPeaks = card("Teetering Peaks") {
    manaCost = ""
    colorIdentity = "R"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n" +
        "When this land enters, target creature gets +2/+0 until end of turn.\n" +
        "{T}: Add {R}."

    replacementEffect(EntersTapped())

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("creature", Targets.Creature)
        effect = Effects.ModifyStats(2, 0, creature)
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "226"
        artist = "Fred Fields"
        imageUri = "https://cards.scryfall.io/normal/front/e/5/e56aca36-bb51-45e3-9ef9-9f9f2aa1e088.jpg"
    }
}
