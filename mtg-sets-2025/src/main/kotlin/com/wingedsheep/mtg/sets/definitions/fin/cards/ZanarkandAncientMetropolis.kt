package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Zanarkand, Ancient Metropolis // Lasting Fayth
 * Land — Town // Sorcery — Adventure
 *
 * Zanarkand, Ancient Metropolis:
 *   This land enters tapped.
 *   {T}: Add {G}.
 *
 * Lasting Fayth — {4}{G}{G}, Sorcery — Adventure:
 *   Create a 1/1 colorless Hero creature token. Put a +1/+1 counter on it for each land you control.
 *   (Then exile this card. You may play the land later from exile.)
 *
 * Town land // spell Adventure — see [IshgardTheHolySee]. The counters target the freshly created
 * token via the [CREATED_TOKENS] pipeline collection ([Effects.AddCountersToCollection]); the count
 * is "lands you control" read through projected control.
 */
val ZanarkandAncientMetropolis = card("Zanarkand, Ancient Metropolis") {
    manaCost = ""
    colorIdentity = "G"
    typeLine = "Land — Town"
    oracleText = "This land enters tapped.\n{T}: Add {G}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    adventure("Lasting Fayth") {
        manaCost = "{4}{G}{G}"
        typeLine = "Sorcery — Adventure"
        oracleText = "Create a 1/1 colorless Hero creature token. Put a +1/+1 counter on it for each " +
            "land you control. (Then exile this card. You may play the land later from exile.)"
        spell {
            effect = Effects.Composite(
                Effects.CreateToken(
                    power = 1,
                    toughness = 1,
                    colors = emptySet(),
                    creatureTypes = setOf("Hero"),
                    imageUri = "https://cards.scryfall.io/normal/front/d/0/d0657ce1-bf75-4007-ac1b-0623eb263357.jpg?1748704030",
                ),
                Effects.AddCountersToCollection(
                    CREATED_TOKENS,
                    Counters.PLUS_ONE_PLUS_ONE,
                    DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Land)
                )
            )
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "293"
        artist = "Erikas Perl"
        imageUri = "https://cards.scryfall.io/normal/front/8/8/881e4c00-3b9a-47a1-bf66-1badda994c88.jpg?1748706876"
    }
}
