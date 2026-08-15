package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Kyoshi Island Plaza
 * {3}{G}
 * Legendary Enchantment — Shrine
 * When Kyoshi Island Plaza enters, search your library for up to X basic land cards, where X is
 * the number of Shrines you control. Put those cards onto the battlefield tapped, then shuffle.
 * Whenever another Shrine you control enters, search your library for a basic land card, put it
 * onto the battlefield tapped, then shuffle.
 *
 * Parallels the Shrine cycle (cf. The Spirit Oasis, Northern Air Temple): the ETB fetches up to
 * one basic land per Shrine you control (this permanent counts itself, as it is already on the
 * battlefield when the trigger resolves), and a second trigger fetches one basic land on every
 * *other* Shrine you control entering ([TriggerBinding.OTHER] so Kyoshi Island Plaza's own entry
 * doesn't fire the second ability).
 */
val KyoshiIslandPlaza = card("Kyoshi Island Plaza") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Enchantment — Shrine"
    oracleText = "When Kyoshi Island Plaza enters, search your library for up to X basic land cards, " +
        "where X is the number of Shrines you control. Put those cards onto the battlefield tapped, " +
        "then shuffle.\n" +
        "Whenever another Shrine you control enters, search your library for a basic land card, put " +
        "it onto the battlefield tapped, then shuffle."

    // X = the number of Shrines you control (this permanent already counts itself on its own ETB).
    val shrinesYouControl = DynamicAmounts
        .battlefield(Player.You, GameObjectFilter.Any.withSubtype("Shrine"))
        .count()

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.BasicLand,
            count = shrinesYouControl,
            destination = SearchDestination.BATTLEFIELD,
            entersTapped = true,
        )
    }

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Any.withSubtype("Shrine").youControl(),
            binding = TriggerBinding.OTHER,
        )
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.BasicLand,
            count = 1,
            destination = SearchDestination.BATTLEFIELD,
            entersTapped = true,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "184"
        artist = "Eilene Cherie"
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9c25ea98-9823-4487-bf6b-ee29ce5a4ed8.jpg?1764121249"
    }
}
