package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sunscorched Desert
 *
 * Land — Desert
 * When this land enters, it deals 1 damage to target player or planeswalker.
 * {T}: Add {C}.
 */
val SunscorchedDesert = card("Sunscorched Desert") {
    typeLine = "Land — Desert"
    oracleText = "When this land enters, it deals 1 damage to target player or planeswalker.\n" +
        "{T}: Add {C}."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val victim = target("target", Targets.PlayerOrPlaneswalker)
        effect = Effects.DealDamage(1, victim)
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "249"
        artist = "Min Yum"
        flavorText = "The only relief in sight is a mirage."
        imageUri = "https://cards.scryfall.io/normal/front/4/0/405434c7-9206-45b7-af0f-d59aae294d39.jpg?1783936445"
    }
}
