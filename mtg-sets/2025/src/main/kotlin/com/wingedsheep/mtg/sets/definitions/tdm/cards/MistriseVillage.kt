package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Mistrise Village — Tarkir: Dragonstorm #261
 * Land · Rare
 *
 * This land enters tapped unless you control a Mountain or a Forest.
 * {T}: Add {U}.
 * {U}, {T}: The next spell you cast this turn can't be countered.
 *
 * Conditional enters-tapped is the check-land replacement [EntersTapped] gated on controlling a
 * Mountain or a Forest (same shape as Kishla Village). {T}: Add {U} is a mana ability. The
 * uncounterable ability ({U} + tap) installs a one-shot rider via
 * [Effects.MakeNextSpellUncounterable] that protects only the next spell its controller casts
 * this turn (no spell filter — any spell).
 */
val MistriseVillage = card("Mistrise Village") {
    typeLine = "Land"
    colorIdentity = "U"
    oracleText = "This land enters tapped unless you control a Mountain or a Forest.\n" +
        "{T}: Add {U}.\n" +
        "{U}, {T}: The next spell you cast this turn can't be countered."

    replacementEffect(EntersTapped(
        unlessCondition = Conditions.Any(
            Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Mountain")),
            Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Forest"))
        )
    ))

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Composite(
            listOf(
                Costs.Mana(ManaCost.parse("{U}")),
                AbilityCost.Tap
            )
        )
        effect = Effects.MakeNextSpellUncounterable()
        description = "The next spell you cast this turn can't be countered."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "261"
        artist = "Constantin Marin"
        imageUri = "https://cards.scryfall.io/normal/front/d/4/d44bccbf-6fab-46e4-8ddb-6577e27ec6e8.jpg?1743205033"
    }
}
