package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Fire Navy Trebuchet
 * {2}{B}
 * Artifact Creature — Wall
 * 0/4
 *
 * Defender, reach
 * Whenever you attack, create a 2/1 colorless Construct artifact creature token with flying
 * named Ballistic Boulder that's tapped and attacking. Sacrifice that token at the beginning
 * of the next end step.
 */
val FireNavyTrebuchet = card("Fire Navy Trebuchet") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Artifact Creature — Wall"
    power = 0
    toughness = 4
    oracleText = "Defender, reach\n" +
        "Whenever you attack, create a 2/1 colorless Construct artifact creature token with " +
        "flying named Ballistic Boulder that's tapped and attacking. Sacrifice that token at " +
        "the beginning of the next end step."

    keywords(Keyword.DEFENDER, Keyword.REACH)

    // Whenever you attack, create a tapped-and-attacking Ballistic Boulder, sacrificed next end step.
    triggeredAbility {
        trigger = Triggers.YouAttack
        effect = CreateTokenEffect(
            power = 2,
            toughness = 1,
            colors = setOf(), // colorless
            creatureTypes = setOf("Construct"),
            keywords = setOf(Keyword.FLYING),
            name = "Ballistic Boulder",
            tapped = true,
            attacking = true,
            artifactToken = true,
            sacrificeAtStep = Step.END
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "100"
        artist = "Mikio Masuda"
        flavorText = "The trebuchets would be destructive enough even without the fire."
        imageUri = "https://cards.scryfall.io/normal/front/e/5/e58372cc-9a89-47a4-a0db-df5435e26cd3.jpg?1764120692"
    }
}
