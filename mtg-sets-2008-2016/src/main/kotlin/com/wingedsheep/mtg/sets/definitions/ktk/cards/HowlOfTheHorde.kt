package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.dsl.Conditions

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Howl of the Horde
 * {2}{R}
 * Sorcery
 *
 * When you next cast an instant or sorcery spell this turn, copy that spell.
 * You may choose new targets for the copy.
 * Raid — If you attacked this turn, when you next cast an instant or sorcery spell
 * this turn, copy that spell an additional time. You may choose new targets for the copy.
 */
val HowlOfTheHorde = card("Howl of the Horde") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "When you next cast an instant or sorcery spell this turn, copy that spell. You may choose new targets for the copy.\nRaid — If you attacked this turn, when you next cast an instant or sorcery spell this turn, copy that spell an additional time. You may choose new targets for the copy."

    spell {
        effect = Effects.CopyNextSpellCast(1) then ConditionalEffect(
            condition = Conditions.YouAttackedThisTurn,
            effect = Effects.CopyNextSpellCast(1)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "112"
        artist = "Slawomir Maniak"
        imageUri = "https://cards.scryfall.io/normal/front/5/d/5db4baf3-f0dc-4b27-8323-a76764332590.jpg?1562911270"
    }
}
