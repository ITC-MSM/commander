package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Forerunner of the Legion
 * {2}{W}
 * Creature — Vampire Knight
 * 2/2
 * When this creature enters, you may search your library for a Vampire card, reveal it, then
 * shuffle and put that card on top.
 * Whenever another Vampire you control enters, target creature gets +1/+1 until end of turn.
 *
 * See [ForerunnerOfTheCoalition] for why the search filter is [GameObjectFilter.Any].
 */
val ForerunnerOfTheLegion = card("Forerunner of the Legion") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Vampire Knight"
    oracleText = "When this creature enters, you may search your library for a Vampire card, " +
        "reveal it, then shuffle and put that card on top.\n" +
        "Whenever another Vampire you control enters, target creature gets +1/+1 until end of turn."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Any.withSubtype(Subtype.VAMPIRE),
            destination = SearchDestination.TOP_OF_LIBRARY,
            reveal = true
        )
    }

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            GameObjectFilter.Permanent.withSubtype(Subtype.VAMPIRE).youControl(),
            TriggerBinding.OTHER
        )
        val boosted = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(1, 1, boosted)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "9"
        artist = "Josh Hass"
        imageUri = "https://cards.scryfall.io/normal/front/1/0/10718d37-63d4-44b7-9450-5d49cffce944.jpg?1783935338"
        ruling(
            "2018-01-19",
            "If an effect refers to a \"[subtype] spell\" or \"[subtype] card,\" it refers only " +
                "to a spell or card that has that subtype. For example, March of the Drowned is " +
                "a card that benefits Pirates and features Pirates in its illustration, but it " +
                "isn't a Pirate card."
        )
    }
}
