package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dream Beavers
 * {B}
 * Creature — Beaver Nightmare
 * 1/1
 *
 * Flying
 * When this creature enters, each opponent loses 1 life and you gain
 * 1 life. Scry 1.
 */
val DreamBeavers = card("Dream Beavers") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Beaver Nightmare"
    oracleText = "Flying\nWhen this creature enters, each opponent loses 1 life and you gain 1 life. Scry 1. (Look at the top card of your library. You may put that card on the bottom.)"
    power = 1
    toughness = 1

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent))
            .then(Effects.GainLife(1))
            .then(Patterns.Library.scry(1))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "62"
        artist = "Alix Branwyn"
        flavorText = "\"We've drained human life force for millennia. Then one day, turtles! Delectable! We'll suck you dry and spit out the shells!\""
        imageUri = "https://cards.scryfall.io/normal/front/6/0/600e3bc1-9777-4057-a11e-4f61582636c6.jpg?1771586853"
    }
}
