package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Evolution Charm
 * {1}{G}
 * Instant
 * Choose one —
 * • Search your library for a basic land card, reveal it, put it into your hand, then shuffle.
 * • Return target creature card from your graveyard to your hand.
 * • Target creature gains flying until end of turn.
 */
val EvolutionCharm = card("Evolution Charm") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Search your library for a basic land card, reveal it, put it into your hand, then shuffle.\n" +
        "• Return target creature card from your graveyard to your hand.\n" +
        "• Target creature gains flying until end of turn."

    spell {
        modal(chooseCount = 1) {
            mode("Search your library for a basic land card, reveal it, put it into your hand, then shuffle") {
                effect = Patterns.Library.searchLibrary(
                    filter = GameObjectFilter.BasicLand,
                    destination = SearchDestination.HAND,
                    reveal = true
                )
            }
            mode("Return target creature card from your graveyard to your hand") {
                val t = target("target", Targets.CreatureCardInYourGraveyard)
                effect = Effects.Move(t, Zone.HAND)
            }
            mode("Target creature gains flying until end of turn") {
                val t = target("target", Targets.Creature)
                effect = Effects.GrantKeyword(Keyword.FLYING, t)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "127"
        artist = "John Avon"
        imageUri = "https://cards.scryfall.io/normal/front/e/4/e4546059-71fe-43c1-9272-3b054e668e3c.jpg"
    }
}
