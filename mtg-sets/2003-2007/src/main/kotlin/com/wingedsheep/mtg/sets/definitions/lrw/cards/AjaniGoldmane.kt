package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.SetBasePowerToughnessDynamicStatic
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Ajani Goldmane - {2}{W}{W}
 * Legendary Planeswalker — Ajani
 * Starting Loyalty: 4
 *
 * +1: You gain 2 life.
 *
 * −1: Put a +1/+1 counter on each creature you control. Those creatures gain vigilance until
 * end of turn.
 *
 * −6: Create a white Avatar creature token. It has "This token's power and toughness are each
 * equal to your life total."
 *
 * The −1 fans out over the creatures you control at resolution, giving each its counter and its
 * vigilance in one pass — the 2007-10-01 ruling says the vigilance stays until end of turn even
 * if the counter is later removed, which the independent `GrantKeyword` floating effect honors.
 * The Avatar's P/T is a characteristic-defining ability, not a snapshot: it is modelled as a
 * [SetBasePowerToughnessDynamicStatic] on the token so it tracks your life total continuously
 * (the second 2007-10-01 ruling).
 */
val AjaniGoldmane = card("Ajani Goldmane") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Planeswalker — Ajani"
    startingLoyalty = 4
    oracleText = "+1: You gain 2 life.\n" +
        "−1: Put a +1/+1 counter on each creature you control. Those creatures gain vigilance until end of turn.\n" +
        "−6: Create a white Avatar creature token. It has \"This token's power and toughness are each equal to your life total.\""

    // +1: You gain 2 life.
    loyaltyAbility(+1) {
        effect = Effects.GainLife(2)
    }

    // −1: Put a +1/+1 counter on each creature you control. Those creatures gain vigilance until end of turn.
    loyaltyAbility(-1) {
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl()),
            Effects.Composite(
                Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
                Effects.GrantKeyword(Keyword.VIGILANCE, EffectTarget.Self)
            )
        )
    }

    // −6: Create a white Avatar creature token with P/T each equal to your life total.
    loyaltyAbility(-6) {
        effect = Effects.CreateToken(
            power = 0,
            toughness = 0,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Avatar"),
            imageUri = "https://cards.scryfall.io/normal/front/f/6/f669cb99-be79-413b-8266-1dfd6a15cb41.jpg?1783942843",
            staticAbilities = listOf(
                SetBasePowerToughnessDynamicStatic(
                    power = DynamicAmount.YourLifeTotal,
                    toughness = DynamicAmount.YourLifeTotal
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "1"
        artist = "Aleksi Briclot"
        imageUri = "https://cards.scryfall.io/normal/front/2/a/2a1470a6-d09d-4a2a-84a6-d56e32ed237a.jpg?1783942919"
        ruling(
            "2007-10-01",
            "The vigilance granted to a creature by the second ability remains until the end of " +
                "the turn even if the +1/+1 counter is removed.",
        )
        ruling(
            "2007-10-01",
            "The power and toughness of the Avatar created by the third ability will change as " +
                "your life total changes.",
        )
    }
}
