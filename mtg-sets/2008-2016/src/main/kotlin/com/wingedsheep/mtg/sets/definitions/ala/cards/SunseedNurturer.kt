package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Sunseed Nurturer
 * {2}{W}
 * Creature — Human Druid Wizard
 * 1 / 1
 * At the beginning of your end step, if you control a creature with power 5 or greater, you may gain 2 life.
 * {T}: Add {C}.
 *
 * The printed "if …" is an intervening-if on [Triggers.YourEndStep], so it is checked both when the
 * ability would trigger and again on resolution — [Conditions.YouControl] over
 * `GameObjectFilter.Creature.powerAtLeast(5)`, the mere-existence form (never
 * `YouControlAtLeast(1, …)`). "You may" is the `optional = true` shorthand, which lowers to a
 * [com.wingedsheep.sdk.scripting.effects.Gate.MayDecide] around [Effects.GainLife]. The mana
 * ability is [Effects.AddColorlessMana]`(1)` on [Costs.Tap], flagged `manaAbility` with
 * [TimingRule.ManaAbility].
 */
val SunseedNurturer = card("Sunseed Nurturer") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Druid Wizard"
    power = 1
    toughness = 1
    oracleText = "At the beginning of your end step, if you control a creature with power 5 or greater, you may gain 2 life.\n" +
        "{T}: Add {C}."

    triggeredAbility {
        trigger = Triggers.YourEndStep
        interveningIf = Conditions.YouControl(GameObjectFilter.Creature.powerAtLeast(5))
        optional = true
        effect = Effects.GainLife(2)
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "29"
        artist = "Steve Argyle"
        flavorText = "Sunseeders quest for areas of open sky. They train plowbeasts to beat back the dense jungle long enough to cultivate a crop."
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9d44447e-09d1-450f-907f-42f79b004fe7.jpg"
    }
}
