package com.wingedsheep.mtg.sets.definitions.eld.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Castle Ardenvale
 * Land
 * This land enters tapped unless you control a Plains.
 * {T}: Add {W}.
 * {2}{W}{W}, {T}: Create a 1/1 white Human creature token.
 */
val CastleArdenvale = card("Castle Ardenvale") {
    typeLine = "Land"
    colorIdentity = "W"
    oracleText = "This land enters tapped unless you control a Plains.\n" +
        "{T}: Add {W}.\n" +
        "{2}{W}{W}, {T}: Create a 1/1 white Human creature token."

    replacementEffect(EntersTapped(
        unlessCondition = Exists(
            Player.You,
            Zone.BATTLEFIELD,
            GameObjectFilter.Land.withSubtype("Plains")
        )
    ))

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{W}{W}"), Costs.Tap)
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Human")
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "238"
        artist = "Volkan Baǵa"
        flavorText = "Without Ardenvale's loyalty, the realm would greedily devour itself."
        imageUri = "https://cards.scryfall.io/normal/front/7/f/7f910495-8bd7-4134-a281-c16fd666d5cc.jpg?1783932580"
    }
}
