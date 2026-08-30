package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Denizen of the Deep
 * {6}{U}{U}
 * Creature — Serpent
 * 11/11
 * When this creature enters, return each other creature you control to its owner's hand.
 *
 * Portal Second Age is the card's earliest real-expansion printing, so the canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives here.
 *
 * "Each **other**" is `excludeSelf` on the group filter — the Denizen itself stays on the
 * battlefield — and the drawback is not optional or targeted, so it also bounces creatures that
 * would be illegal targets. Each card goes to its *owner's* hand, not its controller's.
 */
val DenizenOfTheDeep = card("Denizen of the Deep") {
    manaCost = "{6}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Serpent"
    power = 11
    toughness = 11
    oracleText = "When this creature enters, return each other creature you control to its owner's hand."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Group.returnAllToHand(GroupFilter.OtherCreaturesYouControl)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "35"
        artist = "Anson Maddocks"
        imageUri = "https://cards.scryfall.io/normal/front/6/2/620478b8-47b7-48c5-ac22-1ba4d234c794.jpg?1783946487"
    }
}
