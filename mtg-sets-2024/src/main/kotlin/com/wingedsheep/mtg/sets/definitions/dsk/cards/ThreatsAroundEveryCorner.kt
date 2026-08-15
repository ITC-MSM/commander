package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Threats Around Every Corner
 * {3}{G}
 * Enchantment
 * When this enchantment enters, manifest dread.
 * Whenever a face-down permanent you control enters, search your library for a basic land card,
 * put it onto the battlefield tapped, then shuffle.
 *
 * The enters trigger reuses the shared [Patterns.Library.manifestDread] recipe. The payoff is a
 * standard ANY-binding enters trigger filtered to face-down permanents you control
 * ([GameObjectFilter.Any] + `faceDown().youControl()`) — note the manifest-dread permanent from the
 * first trigger itself counts, so it fetches a land too. The fetch is the shared
 * [Patterns.Library.searchLibrary] pipeline restricted to basic lands, entering the battlefield
 * tapped, shuffling afterward.
 */
val ThreatsAroundEveryCorner = card("Threats Around Every Corner") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, manifest dread. (Look at the top two cards of your " +
        "library. Put one onto the battlefield face down as a 2/2 creature and the other into your " +
        "graveyard.)\n" +
        "Whenever a face-down permanent you control enters, search your library for a basic land " +
        "card, put it onto the battlefield tapped, then shuffle."

    // When this enchantment enters, manifest dread.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.manifestDread()
    }

    // Whenever a face-down permanent you control enters, search your library for a basic land
    // card, put it onto the battlefield tapped, then shuffle.
    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Any.faceDown().youControl(),
            binding = TriggerBinding.ANY
        )
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.BasicLand,
            destination = SearchDestination.BATTLEFIELD,
            entersTapped = true,
            shuffleAfter = true
        )
        description = "Search your library for a basic land card, put it onto the battlefield " +
            "tapped, then shuffle."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "200"
        artist = "Andrea Piparo"
        imageUri = "https://cards.scryfall.io/normal/front/7/2/7201ee12-9104-48d8-aeec-08a318c8ee10.jpg?1726286614"
    }
}
