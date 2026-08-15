package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Fleeting Effigy
 * {R}
 * Creature — Elemental
 * 2/2
 * Haste
 * At the beginning of your end step, return this creature to its owner's hand.
 * {2}{R}: This creature gets +2/+0 until end of turn.
 */
val FleetingEffigy = card("Fleeting Effigy") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental"
    power = 2
    toughness = 2
    oracleText = "Haste\n" +
        "At the beginning of your end step, return this creature to its owner's hand. " +
        "(Return it only if it's on the battlefield.)\n" +
        "{2}{R}: This creature gets +2/+0 until end of turn."

    keywords(Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.YourEndStep
        effect = Effects.ReturnToHand(EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Mana("{2}{R}")
        effect = Effects.ModifyStats(2, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "108"
        artist = "Darren Tan"
        imageUri = "https://cards.scryfall.io/normal/front/1/9/1971fd6c-0a1c-41b2-93a6-886a176fbb73.jpg?1743697583"
    }
}
