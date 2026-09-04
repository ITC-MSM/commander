package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Forerunner of the Heralds
 * {3}{G}
 * Creature — Merfolk Scout
 * 3/2
 * When this creature enters, you may search your library for a Merfolk card, reveal it, then
 * shuffle and put that card on top.
 * Whenever another Merfolk you control enters, put a +1/+1 counter on this creature.
 *
 * See [ForerunnerOfTheCoalition] for why the search filter is [GameObjectFilter.Any].
 */
val ForerunnerOfTheHeralds = card("Forerunner of the Heralds") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Merfolk Scout"
    oracleText = "When this creature enters, you may search your library for a Merfolk card, " +
        "reveal it, then shuffle and put that card on top.\n" +
        "Whenever another Merfolk you control enters, put a +1/+1 counter on this creature."
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Any.withSubtype(Subtype.MERFOLK),
            destination = SearchDestination.TOP_OF_LIBRARY,
            reveal = true
        )
    }

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            GameObjectFilter.Permanent.withSubtype(Subtype.MERFOLK).youControl(),
            TriggerBinding.OTHER
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "129"
        artist = "Chris Seaman"
        imageUri = "https://cards.scryfall.io/normal/front/3/0/30bc2bd2-adfc-490e-998a-303598e6a942.jpg?1783935288"
        ruling(
            "2018-01-19",
            "If an effect refers to a \"[subtype] spell\" or \"[subtype] card,\" it refers only " +
                "to a spell or card that has that subtype. For example, March of the Drowned is " +
                "a card that benefits Pirates and features Pirates in its illustration, but it " +
                "isn't a Pirate card."
        )
    }
}
