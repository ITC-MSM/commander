package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantCardType
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.station

/**
 * Infinite Guideline Station
 * {W}{U}{B}{R}{G}
 * Legendary Artifact — Spacecraft
 * 7/15
 *
 * When Infinite Guideline Station enters, create a tapped 2/2 colorless Robot artifact creature
 * token for each multicolored permanent you control.
 * Station (Tap another creature you control: Put charge counters equal to its power on this
 * Spacecraft. Station only as a sorcery. It's an artifact creature at 12+.)
 * 12+ | Flying
 * Whenever Infinite Guideline Station attacks, draw a card for each multicolored permanent you control.
 */
val InfiniteGuidelineStation = card("Infinite Guideline Station") {
    manaCost = "{W}{U}{B}{R}{G}"
    colorIdentity = "WUBRG"
    typeLine = "Legendary Artifact — Spacecraft"
    power = 7
    toughness = 15
    oracleText = "When Infinite Guideline Station enters, create a tapped 2/2 colorless Robot artifact " +
        "creature token for each multicolored permanent you control.\n" +
        "Station (Tap another creature you control: Put charge counters equal to its power on this " +
        "Spacecraft. Station only as a sorcery. It's an artifact creature at 12+.)\n12+ | Flying\n" +
        "Whenever Infinite Guideline Station attacks, draw a card for each multicolored permanent you control."

    // ETB: create a tapped 2/2 colorless Robot artifact creature token for each multicolored permanent.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = CreateTokenEffect(
            count = DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Multicolored),
            name = "Robot",
            power = 2,
            toughness = 2,
            colors = emptySet(),
            creatureTypes = setOf("Robot"),
            artifactToken = true,
            tapped = true,
            imageUri = "https://cards.scryfall.io/normal/front/c/4/c46f9a07-005c-44b7-8057-b2f00b274dd6.jpg?1756281130"
        )
        description = "create a tapped 2/2 colorless Robot artifact creature token for each multicolored permanent you control"
    }

    // Station activated ability: tap another creature → add charge counters equal to its power
    station()

    // Conditional type change: artifact creature at 12+ charge counters
    staticAbility {
        condition = Conditions.SourceCounterCountAtLeast(Counters.CHARGE, 12)
        ability = GrantCardType("CREATURE", GroupFilter.source())
    }

    // Conditional keyword: flying at 12+ charge counters
    staticAbility {
        condition = Conditions.SourceCounterCountAtLeast(Counters.CHARGE, 12)
        ability = GrantKeyword(Keyword.FLYING.name, GroupFilter.source())
    }

    // Whenever this attacks, draw a card for each multicolored permanent you control.
    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.DrawCards(DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Multicolored))
        description = "Whenever Infinite Guideline Station attacks, draw a card for each multicolored permanent you control."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "219"
        artist = "Piotr Dura"
        imageUri = "https://cards.scryfall.io/normal/front/5/6/5688894a-bbec-476b-ae2e-94000be258d0.jpg?1755341280"
    }
}
