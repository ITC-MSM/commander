package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Basic Conjuration — Strixhaven: School of Mages #120 (canonical printing)
 * {1}{G}{G} · Sorcery — Lesson
 *
 * Look at the top six cards of your library. You may reveal a creature card from among them and put it into your hand. Put the rest on the bottom of your library in a random order. You gain 3 life.
 *
 * The look-at-the-top dig is [Patterns.Library.lookAtTopRevealMatchingToHand] — a private look at
 * six, an optional filtered pick that is revealed as it goes to hand, and the rest to the bottom in
 * a random order (its defaults). The life gain follows as a plain [Effects.GainLife] in printed
 * order. Lesson is only a subtype.
 */
val BasicConjuration = card("Basic Conjuration") {
    manaCost = "{1}{G}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery — Lesson"
    oracleText =
        "Look at the top six cards of your library. You may reveal a creature card from among them and put it into your hand. Put the rest on the bottom of your library in a random order. You gain 3 life."

    spell {
        effect = Patterns.Library.lookAtTopRevealMatchingToHand(
            count = DynamicAmount.Fixed(6),
            filter = GameObjectFilter.Creature,
            prompt = "You may reveal a creature card from among them and put it into your hand"
        ) then Effects.GainLife(3)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "120"
        artist = "Randy Vargas"
        flavorText = "\"I made that! It's mine! Did you see that?\""
        imageUri = "https://cards.scryfall.io/normal/front/8/b/8be52d88-f430-4437-a0d3-590c2947c838.jpg?1783927348"
    }
}
