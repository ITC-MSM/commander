package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ruric Thar, Biomagus — two instances of prowess, each its own trigger (CR 702.108b), as on
 * Thor Odinson.
 */
val RuricTharBiomagus = card("Ruric Thar, Biomagus") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Ogre Crab Wizard"
    power = 4
    toughness = 6
    oracleText = "Flying\nProwess, prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn twice.)\nWhenever Ruric Thar becomes the target of a spell or ability an opponent controls, draw a card."

    keywords(Keyword.FLYING)
    prowess()
    prowess()

    triggeredAbility {
        trigger = Triggers.BecomesTargetByOpponent
        effect = Effects.DrawCards(1)
        description = "Whenever Ruric Thar becomes the target of a spell or ability an opponent controls, draw a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "219"
        artist = "Olivier Bernard"
        flavorText = "The Simic Combine's two brightest minds."
        imageUri = "https://cards.scryfall.io/normal/front/0/0/00af4e87-5576-4a43-9422-4c35b2b66775.jpg?1789127229"
        inBooster = false
    }
}
