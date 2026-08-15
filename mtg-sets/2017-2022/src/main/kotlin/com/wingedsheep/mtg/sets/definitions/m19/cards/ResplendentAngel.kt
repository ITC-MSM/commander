package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Resplendent Angel
 * {1}{W}{W}
 * Creature — Angel
 * 3/3
 *
 * Flying
 * At the beginning of each end step, if you gained 5 or more life this turn, create a 4/4 white
 * Angel creature token with flying and vigilance.
 * {3}{W}{W}{W}: Until end of turn, this creature gets +2/+2 and gains lifelink.
 *
 * The token trigger is an intervening-"if" ability: it only triggers if you gained 5 or more life
 * total during the turn before the end step begins (net change / other life loss is irrelevant;
 * life gained during the end step itself is too late), and it re-checks on resolution.
 */
val ResplendentAngel = card("Resplendent Angel") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    oracleText = "Flying\n" +
        "At the beginning of each end step, if you gained 5 or more life this turn, create a 4/4 " +
        "white Angel creature token with flying and vigilance.\n" +
        "{3}{W}{W}{W}: Until end of turn, this creature gets +2/+2 and gains lifelink."
    power = 3
    toughness = 3

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EachEndStep
        triggerCondition = Conditions.YouGainedLifeThisTurnAtLeast(5)
        effect = Effects.CreateToken(
            power = 4,
            toughness = 4,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Angel"),
            keywords = setOf(Keyword.FLYING, Keyword.VIGILANCE),
            imageUri = "https://cards.scryfall.io/normal/front/d/c/dc31e017-81e6-43bf-bf8e-ea0faca5d9b7.jpg?1782747632"
        )
    }

    activatedAbility {
        cost = Costs.Mana("{3}{W}{W}{W}")
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
            .then(Effects.GrantKeyword(Keyword.LIFELINK, EffectTarget.Self))
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "34"
        artist = "Volkan Baǵa"
        imageUri = "https://cards.scryfall.io/normal/front/5/8/586854d1-edfd-4c66-873d-df459324dbfd.jpg?1782709621"

        ruling("2018-07-13", "Resplendent Angel's triggered ability checks if you gained 5 or more life total during the turn. It doesn't matter if you also lost life or whether your life total is greater than it was at the beginning of the turn. It also doesn't matter whether Resplendent Angel was on the battlefield when any of the life gain happened.")
        ruling("2018-07-13", "You don't need to have gained 5 life all at once to satisfy Resplendent Angel's triggered ability.")
        ruling("2018-07-13", "If you didn't gain life during the turn before the end step begins, Resplendent Angel's triggered ability won't trigger at all. Gaining life during the end step won't cause the ability to trigger.")
        ruling("2018-07-13", "You create only one Angel token, no matter how many times you gained 5 or more life.")
    }
}
