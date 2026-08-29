package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sphinx Mindbreaker
 * {5}{U}{U}
 * Creature — Sphinx
 * 6/6
 *
 * Flying
 * When this creature enters, each opponent mills ten cards.
 *
 * The miller is a parameter of the mill recipe, not a wrapper around it: passing
 * `EffectTarget.PlayerRef(Player.EachOpponent)` puts `EachOpponent` on both the gather's
 * `TopOfLibrary` and the move's `ToZone`, so one pipeline fans out over every opponent's library.
 * A `ForEachPlayerEffect` around a controller-scoped mill would be a different model.
 */
val SphinxMindbreaker = card("Sphinx Mindbreaker") {
    manaCost = "{5}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Sphinx"
    power = 6
    toughness = 6
    oracleText = "Flying\n" +
        "When this creature enters, each opponent mills ten cards."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.mill(10, EffectTarget.PlayerRef(Player.EachOpponent))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "290"
        artist = "PINDURSKI"
        flavorText = "Riddles draw you in, paradoxes hold you fast, and answers shatter your perceptions."
        imageUri = "https://cards.scryfall.io/normal/front/d/7/d70c50ce-48f0-49a6-9653-42b8ccb9a647.jpg"
        inBooster = false
    }
}
