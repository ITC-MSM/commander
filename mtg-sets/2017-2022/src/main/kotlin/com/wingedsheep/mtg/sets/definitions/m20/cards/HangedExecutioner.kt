package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hanged Executioner
 * {2}{W}
 * Creature — Spirit
 * 1/1
 *
 * Flying
 * When this creature enters, create a 1/1 white Spirit creature token with flying.
 * {3}{W}, Exile this creature: Exile target creature.
 */
val HangedExecutioner = card("Hanged Executioner") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit"
    oracleText = "Flying\n" +
        "When this creature enters, create a 1/1 white Spirit creature token with flying.\n" +
        "{3}{W}, Exile this creature: Exile target creature."
    power = 1
    toughness = 1

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Spirit"),
            keywords = setOf(Keyword.FLYING)
        )
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{W}"), Costs.ExileSelf)
        val victim = target("target creature", Targets.Creature)
        effect = Effects.Exile(victim)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "22"
        artist = "Johann Bodin"
        imageUri = "https://cards.scryfall.io/normal/front/e/0/e0d6cea1-83d1-4045-b4da-40560af86df9.jpg?1783933026"
    }
}
