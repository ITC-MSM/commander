package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Forerunner of the Coalition
 * {2}{B}
 * Creature — Human Pirate
 * 2/2
 * When this creature enters, you may search your library for a Pirate card, reveal it, then
 * shuffle and put that card on top.
 * Whenever another Pirate you control enters, each opponent loses 1 life.
 *
 * "A Pirate card" is any card type carrying the subtype, so the search filter is
 * [GameObjectFilter.Any]. `Patterns.Library.searchLibrary`'s defaults (`count = 1`,
 * `shuffleAfter = true`) give the printed shuffle-then-put-on-top order.
 */
val ForerunnerOfTheCoalition = card("Forerunner of the Coalition") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Pirate"
    oracleText = "When this creature enters, you may search your library for a Pirate card, " +
        "reveal it, then shuffle and put that card on top.\n" +
        "Whenever another Pirate you control enters, each opponent loses 1 life."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Any.withSubtype(Subtype.PIRATE),
            destination = SearchDestination.TOP_OF_LIBRARY,
            reveal = true
        )
    }

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            GameObjectFilter.Permanent.withSubtype(Subtype.PIRATE).youControl(),
            TriggerBinding.OTHER
        )
        effect = Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "72"
        artist = "James Ryman"
        imageUri = "https://cards.scryfall.io/normal/front/b/4/b499fc26-26b0-4b0f-9c62-5ed599baf41d.jpg?1783935311"
        ruling(
            "2018-01-19",
            "In a Two-Headed Giant game, the last ability of Forerunner of the Coalition causes " +
                "the opposing team to lose 2 life."
        )
        ruling(
            "2018-01-19",
            "If an effect refers to a \"[subtype] spell\" or \"[subtype] card,\" it refers only " +
                "to a spell or card that has that subtype. For example, March of the Drowned is " +
                "a card that benefits Pirates and features Pirates in its illustration, but it " +
                "isn't a Pirate card."
        )
    }
}
