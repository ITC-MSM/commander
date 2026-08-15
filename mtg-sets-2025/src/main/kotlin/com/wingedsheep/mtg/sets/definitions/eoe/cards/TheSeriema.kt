package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantCardType
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.station

/**
 * The Seriema
 * {1}{W}{W}
 * Legendary Artifact — Spacecraft
 * When The Seriema enters, search your library for a legendary creature card, reveal it,
 * put it into your hand, then shuffle.
 * Station (Tap another creature you control: Put charge counters equal to its power on this
 * Spacecraft. Station only as a sorcery. It's an artifact creature at 7+.)
 * 7+ | Flying
 * Other tapped legendary creatures you control have indestructible.
 * 5/5
 */
val TheSeriema = card("The Seriema") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Artifact — Spacecraft"
    power = 5
    toughness = 5
    oracleText = "When The Seriema enters, search your library for a legendary creature card, reveal it, put it into your hand, then shuffle.\n" +
        "Station (Tap another creature you control: Put charge counters equal to its power on this Spacecraft. Station only as a sorcery. It's an artifact creature at 7+.)\n" +
        "7+ | Flying\n" +
        "Other tapped legendary creatures you control have indestructible."

    // When The Seriema enters, search your library for a legendary creature card, reveal it,
    // put it into your hand, then shuffle.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Creature.legendary(),
            count = 1,
            destination = SearchDestination.HAND,
            reveal = true,
            shuffleAfter = true
        )
        description = "When The Seriema enters, search your library for a legendary creature card, reveal it, put it into your hand, then shuffle."
    }

    // Station activated ability: tap another creature → add charge counters equal to its power.
    station()

    val charge7 = Conditions.SourceCounterCountAtLeast(Counters.CHARGE, 7)

    // 7+ charge counters: becomes an artifact creature.
    staticAbility {
        condition = charge7
        ability = GrantCardType("CREATURE", GroupFilter.source())
    }

    // 7+ charge counters: Flying.
    staticAbility {
        condition = charge7
        ability = GrantKeyword(Keyword.FLYING, GroupFilter.source())
    }

    // Other tapped legendary creatures you control have indestructible.
    staticAbility {
        ability = GrantKeyword(
            Keyword.INDESTRUCTIBLE,
            GroupFilter(
                GameObjectFilter.Creature.legendary().youControl().tapped(),
                excludeSelf = true
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "35"
        artist = "Sergey Glushakov"
        imageUri = "https://cards.scryfall.io/normal/front/d/e/dec91ec3-42d3-4922-96f0-dbb50a576084.jpg?1755341202"
    }
}
