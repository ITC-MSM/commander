package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Guardian of Cloverdell
 * {5}{G}{G}
 * Creature — Treefolk Shaman
 * 4/5
 * When this creature enters, create three 1/1 white Kithkin Soldier creature tokens.
 * {G}, Sacrifice a Kithkin: You gain 1 life.
 */
val GuardianOfCloverdell = card("Guardian of Cloverdell") {
    manaCost = "{5}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Treefolk Shaman"
    power = 4
    toughness = 5
    oracleText = "When this creature enters, create three 1/1 white Kithkin Soldier creature tokens.\n" +
        "{G}, Sacrifice a Kithkin: You gain 1 life."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Kithkin", "Soldier"),
            count = 3,
            imageUri = "https://cards.scryfall.io/normal/front/a/d/ad29eb21-7ee3-4a67-9601-a62ea0cbe4c0.jpg?1783942839",
        )
        description = "create three 1/1 white Kithkin Soldier creature tokens."
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{G}"),
            Costs.Sacrifice(GameObjectFilter.Permanent.withSubtype(Subtype.KITHKIN))
        )
        effect = Effects.GainLife(1)
        description = "{G}, Sacrifice a Kithkin: You gain 1 life."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "216"
        artist = "Jesper Ejsing"
        flavorText = "Although they're protective of all creatures, many treefolk are especially fond of the empathic kithkin."
        imageUri = "https://cards.scryfall.io/normal/front/3/1/311c5f1d-7e3b-4397-b05b-f20bde2dc164.jpg?1783942863"
    }
}
