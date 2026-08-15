package com.wingedsheep.mtg.sets.definitions.c17.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Ramos, Dragon Engine — Commander 2017 #55.
 *
 * The cast trigger counts the triggering spell's colors, not the colors of mana spent to cast it.
 */
val RamosDragonEngine = card("Ramos, Dragon Engine") {
    manaCost = "{6}"
    colorIdentity = "WUBRG"
    typeLine = "Legendary Artifact Creature — Dragon"
    power = 4
    toughness = 4
    oracleText = "Flying\nWhenever you cast a spell, put a +1/+1 counter on Ramos for each of that spell's colors.\nRemove five +1/+1 counters from Ramos: Add {W}{W}{U}{U}{B}{B}{R}{R}{G}{G}. Activate only once each turn."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YouCastSpell
        effect = Effects.AddDynamicCounters(
            counterType = Counters.PLUS_ONE_PLUS_ONE,
            amount = DynamicAmounts.colorCountOf(EntityReference.Triggering),
            target = EffectTarget.Self
        )
    }

    activatedAbility {
        cost = Costs.RemoveCounterFromSelf(Counters.PLUS_ONE_PLUS_ONE, 5)
        restrictions = listOf(ActivationRestriction.OncePerTurn)
        effect = Effects.Composite(
            Effects.AddMana(Color.WHITE, 2),
            Effects.AddMana(Color.BLUE, 2),
            Effects.AddMana(Color.BLACK, 2),
            Effects.AddMana(Color.RED, 2),
            Effects.AddMana(Color.GREEN, 2)
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "55"
        artist = "Joseph Meehan"
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2e747ef1-a1ad-4859-a70c-3f935f017310.jpg?1783935930"

        ruling("2024-11-08", "Ramos's triggered ability counts the number of colors a spell has (from zero to five), not how many colored mana symbols are there in its mana cost or how many colors of mana you spent.")
        ruling("2024-11-08", "If you cast a colorless spell, Ramos's triggered ability triggers, but it won't get any +1/+1 counters.")
        ruling("2024-11-08", "Ramos's triggered ability resolves before the spell that caused it to trigger. The ability will resolve even if that spell is countered or otherwise leaves the stack without resolving. In that case, use its last known information to determine what colors it was.")
    }
}
