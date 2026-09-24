package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.FeasibilityCheck
import com.wingedsheep.sdk.scripting.effects.IfYouDoEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Entrust the Spark — "You may sacrifice a planeswalker. If you do, …" is a resolution-time
 * [MayEffect] around an [IfYouDoEffect]: the sacrifice is a Gather → Select → Sacrifice pipeline
 * over the planeswalkers you control, and only when one was actually sacrificed does the library
 * search (for any planeswalker card, straight onto the battlefield, then shuffle) run. With no
 * planeswalker to sacrifice the "may" isn't offered and the spell does nothing.
 */
val EntrustTheSpark = card("Entrust the Spark") {
    manaCost = "{3}{G}{U}"
    colorIdentity = "GU"
    typeLine = "Sorcery"
    oracleText = "You may sacrifice a planeswalker. If you do, search your library for a planeswalker " +
        "card, put it onto the battlefield, then shuffle."

    spell {
        effect = MayEffect(
            effect = IfYouDoEffect(
                action = Effects.Pipeline {
                    val planeswalkers = gather(GameObjectFilter.Planeswalker, player = Player.You)
                    val chosen = chooseExactly(
                        1,
                        from = planeswalkers,
                        useTargetingUI = true,
                        prompt = "Choose a planeswalker to sacrifice",
                    )
                    sacrifice(chosen)
                },
                ifYouDo = Patterns.Library.searchLibrary(
                    filter = GameObjectFilter.Planeswalker,
                    destination = SearchDestination.BATTLEFIELD,
                ),
            ),
            descriptionOverride = "You may sacrifice a planeswalker. If you do, search your library " +
                "for a planeswalker card, put it onto the battlefield, then shuffle.",
            feasibility = FeasibilityCheck.ControlsPermanentMatching(GameObjectFilter.Planeswalker.youControl()),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "131"
        artist = "Justyna Dura"
        flavorText = "\"A spark is power and freedom. Use it better than I did,\" Jace's voice echoed to Tam."
        imageUri = "https://cards.scryfall.io/normal/front/c/a/ca894d25-b9fc-4cd6-8746-70d8c2868721.jpg?1789614779"
        inBooster = false
    }
}
