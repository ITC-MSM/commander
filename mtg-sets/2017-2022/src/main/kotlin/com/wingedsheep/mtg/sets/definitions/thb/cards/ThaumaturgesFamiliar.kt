package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Thaumaturge's Familiar
 * {3}
 * Artifact Creature — Bird
 * 1/3
 * Flying
 * When this creature enters, scry 1.
 */
val ThaumaturgesFamiliar = card("Thaumaturge's Familiar") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Bird"
    power = 1
    toughness = 3
    oracleText = "Flying\nWhen this creature enters, scry 1."

    keywords(Keyword.FLYING)

    // When this creature enters, scry 1.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.scry(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "238"
        artist = "Andrea Radeck"
        flavorText = "Meletian mages can't claim the title of thaumaturge until they have received a gift or omen from the gods. Ephara likes to give tangible tokens of her favor."
        imageUri = "https://cards.scryfall.io/normal/front/7/b/7b497153-69a8-480e-b02f-88afec9d5053.jpg?1783931515"
    }
}
