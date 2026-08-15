package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Demolition Field
 * Land
 * {T}: Add {C}.
 * {2}, {T}, Sacrifice this land: Destroy target nonbasic land an opponent controls. That land's
 * controller may search their library for a basic land card, put it onto the battlefield, then
 * shuffle. You may search your library for a basic land card, put it onto the battlefield, then
 * shuffle.
 *
 * "That land's controller" is [Player.ControllerOf] the destroyed land — resolved after the land
 * dies, so in the normal case (an opponent controlling their own land) it reads the graveyard
 * card's owner, which is that same player. Their optional basic-land fetch runs under
 * [Effects.ForEachPlayer], which rebinds `Player.You` inside the search to that player; the
 * controller's own optional fetch follows as a plain `Player.You` search.
 */
val DemolitionField = card("Demolition Field") {
    manaCost = ""
    colorIdentity = ""
    typeLine = "Land"
    oracleText = "{T}: Add {C}.\n" +
        "{2}, {T}, Sacrifice this land: Destroy target nonbasic land an opponent controls. " +
        "That land's controller may search their library for a basic land card, put it onto the " +
        "battlefield, then shuffle. You may search your library for a basic land card, put it onto " +
        "the battlefield, then shuffle."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap, Costs.SacrificeSelf)
        val land = target(
            "target nonbasic land an opponent controls",
            TargetPermanent(filter = TargetFilter.NonbasicLand.opponentControls())
        )
        effect = Effects.Destroy(land) then
            Effects.ForEachPlayer(
                Player.ControllerOf("the destroyed land"),
                listOf(
                    MayEffect(
                        Patterns.Library.searchLibrary(
                            filter = GameObjectFilter.BasicLand,
                            count = 1,
                            destination = SearchDestination.BATTLEFIELD
                        )
                    )
                )
            ) then
            MayEffect(
                Patterns.Library.searchLibrary(
                    filter = GameObjectFilter.BasicLand,
                    count = 1,
                    destination = SearchDestination.BATTLEFIELD
                )
            )
        description = "{2}, {T}, Sacrifice this land: Destroy target nonbasic land an opponent " +
            "controls. That land's controller may search their library for a basic land card, put " +
            "it onto the battlefield, then shuffle. You may search your library for a basic land " +
            "card, put it onto the battlefield, then shuffle."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "260"
        artist = "Kamila Szutenberg"
        imageUri = "https://cards.scryfall.io/normal/front/d/9/d9c88546-13c9-4d7e-a618-cb2ccd1dbc0f.jpg?1782699688"
    }
}
