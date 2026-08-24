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
 * Looming Spires
 * Land
 * This land enters tapped.
 * When this land enters, target creature gets +1/+1 and gains first strike until end of turn.
 * {T}: Add {R}.
 */
val LoomingSpires = card("Looming Spires") {
    manaCost = ""
    colorIdentity = "R"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n" +
        "When this land enters, target creature gets +1/+1 and gains first strike until end of turn.\n" +
        "{T}: Add {R}."

    replacementEffect(EntersTapped())

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(1, 1, creature),
            Effects.GrantKeyword(Keyword.FIRST_STRIKE, creature),
        )
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "238"
        artist = "Florian de Gesincourt"
        imageUri = "https://cards.scryfall.io/normal/front/b/8/b88177a2-de41-417d-a8f1-07edf005b453.jpg?1783938174"
    }
}
