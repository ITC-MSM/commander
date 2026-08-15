package com.wingedsheep.mtg.sets.definitions.lgn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tribal Forcemage
 * {1}{G}
 * Creature — Elf Wizard
 * 1/1
 * Morph {1}{G} (You may cast this card face down as a 2/2 creature for {3}. Turn it face up any time for its morph cost.)
 * When this creature is turned face up, creatures of the creature type of your choice get +2/+2 and gain trample until end of turn.
 */
val TribalForcemage = card("Tribal Forcemage") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Wizard"
    power = 1
    toughness = 1
    oracleText = "Morph {1}{G} (You may cast this card face down as a 2/2 creature for {3}. Turn it face up any time for its morph cost.)\nWhen this creature is turned face up, creatures of the creature type of your choice get +2/+2 and gain trample until end of turn."

    triggeredAbility {
        trigger = Triggers.TurnedFaceUp
        effect = Effects.ChooseCreatureTypeModifyStats(
            powerModifier = 2,
            toughnessModifier = 2,
            grantKeyword = Keyword.TRAMPLE
        )
    }

    morph = "{1}{G}"

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "142"
        artist = "Greg Staples"
        imageUri = "https://cards.scryfall.io/normal/front/1/0/104735d7-6cea-4d4a-8cc8-e1934883da97.jpg?1562898295"
    }
}
