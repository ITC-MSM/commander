package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Weather Maker
 * {3}
 * Artifact
 *
 * Landfall — Whenever a land you control enters, put a charge counter
 * on this artifact.
 * {T}: Add one mana of any color.
 * {T}, Remove two charge counters from this artifact: Add {C}{C}.
 * {T}, Remove three charge counters from this artifact: It deals 3
 * damage to any target.
 */
val WeatherMaker = card("Weather Maker") {
    manaCost = "{3}"
    typeLine = "Artifact"
    oracleText = "Landfall — Whenever a land you control enters, put a charge counter on this artifact.\n{T}: Add one mana of any color.\n{T}, Remove two charge counters from this artifact: Add {C}{C}.\n{T}, Remove three charge counters from this artifact: It deals 3 damage to any target."

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.AddCounters(Counters.CHARGE, 1, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Tap,
            Costs.RemoveCounterFromSelf(Counters.CHARGE, 2)
        )
        effect = Effects.AddColorlessMana(2)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Tap,
            Costs.RemoveCounterFromSelf(Counters.CHARGE, 3)
        )
        val anyTarget = target("any target", Targets.Any)
        effect = Effects.DealDamage(3, anyTarget)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "182"
        artist = "Florent Lebrun"
        imageUri = "https://cards.scryfall.io/normal/front/a/a/aaba62c1-760b-491b-8047-869bb1d3f67e.jpg?1769006491"
    }
}
