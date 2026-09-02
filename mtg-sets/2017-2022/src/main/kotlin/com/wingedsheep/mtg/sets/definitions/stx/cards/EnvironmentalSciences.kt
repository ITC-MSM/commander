package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Environmental Sciences — Strixhaven: School of Mages #1 (canonical printing)
 * {2} · Sorcery — Lesson
 *
 * Search your library for a basic land card, reveal it, put it into your hand, then shuffle. You gain 2 life.
 *
 * The search is [Patterns.Library.searchLibrary] with the basic-land filter, the hand as its
 * default destination, the found card revealed, and the shuffle plus the "library searched" event
 * it always appends. The life gain follows as a plain [Effects.GainLife] in printed order. Lesson
 * is only a subtype.
 */
val EnvironmentalSciences = card("Environmental Sciences") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Sorcery — Lesson"
    oracleText =
        "Search your library for a basic land card, reveal it, put it into your hand, then shuffle. You gain 2 life."

    spell {
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.BasicLand,
            reveal = true
        ) then Effects.GainLife(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "1"
        artist = "Jokubas Uogintas"
        flavorText = "First-years quickly learn how the Vastlands earned its name."
        imageUri = "https://cards.scryfall.io/normal/front/4/6/46b394fc-a99c-44e7-9226-da0699167541.jpg?1783927397"
    }
}
