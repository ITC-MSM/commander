package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Assassin's Trophy
 * {B}{G}
 * Instant
 *
 * Destroy target permanent an opponent controls. Its controller may search their library for a
 * basic land card, put it onto the battlefield, then shuffle.
 *
 * Canonical printing is Guilds of Ravnica (GRN #152); Murders at Karlov Manor carries only a
 * [com.wingedsheep.sdk.model.Printing] row.
 *
 * The Volatile Fault / Demolition Field shape, widened from "nonbasic land" to *any* permanent an
 * opponent controls. The consolation fetch belongs to the destroyed permanent's controller, not to
 * the caster, so it runs under [Effects.ForEachPlayer] with [Player.ControllerOf] — that rebinds
 * `Player.You` inside the search to that player, which is what makes
 * [Patterns.Library.searchLibrary] (hard-wired to `Player.You`) read the right library.
 *
 * Two printed rulings fall out of the ordering rather than needing a gate of their own:
 * the fetch is *not* conditional on the destruction actually happening, so an indestructible
 * target still hands its controller the basic land; and because the spell fizzles wholesale on an
 * illegal target, no player searches in that case. The [MayEffect] wrapper carries the third —
 * declining the search skips the shuffle with it, since `shuffleAfter` lives inside the search
 * pipeline that never runs.
 */
val AssassinsTrophy = card("Assassin's Trophy") {
    manaCost = "{B}{G}"
    colorIdentity = "BG"
    typeLine = "Instant"
    oracleText = "Destroy target permanent an opponent controls. Its controller may search their " +
        "library for a basic land card, put it onto the battlefield, then shuffle."

    spell {
        val permanent = target("target permanent an opponent controls", Targets.PermanentOpponentControls)
        effect = Effects.Destroy(permanent) then
            Effects.ForEachPlayer(
                Player.ControllerOf("the destroyed permanent"),
                listOf(
                    MayEffect(
                        Patterns.Library.searchLibrary(
                            filter = GameObjectFilter.BasicLand,
                            count = 1,
                            destination = SearchDestination.BATTLEFIELD
                        )
                    )
                )
            )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "152"
        artist = "Seb McKinnon"
        flavorText = "A power vacuum for the Azorius. A keepsake for Vraska."
        imageUri = "https://cards.scryfall.io/normal/front/9/0/906b6e99-128f-4c11-8daf-16099d35b0d4.jpg?1783934143"

        ruling(
            "2018-10-05",
            "If the target permanent is a legal target but isn't destroyed, most likely because " +
                "it has indestructible, its controller may search their library."
        )
        ruling(
            "2018-10-05",
            "If the permanent's controller doesn't search their library, they don't shuffle " +
                "their library."
        )
    }
}
