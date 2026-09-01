package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Clarion Spirit
 * {1}{W}
 * Creature — Spirit
 * 2/2
 * Whenever you cast your second spell each turn, create a 1/1 white Spirit creature token with flying.
 *
 * "Whenever you cast your second spell each turn" is [Triggers.NthSpellCast] with n = 2 scoped to
 * [Player.You] — the engine already tracks each player's per-turn cast count, so the ordinal needs
 * no bookkeeping on the card.
 */
val ClarionSpirit = card("Clarion Spirit") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit"
    oracleText = "Whenever you cast your second spell each turn, create a 1/1 white Spirit creature token with flying."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.NthSpellCast(2, Player.You)
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Spirit"),
            keywords = setOf(Keyword.FLYING)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "6"
        artist = "Anastasia Ovchinnikova"
        flavorText = "To the living, the horn sounds faint and mournful, but to the spirits of Istfell, it is a thunderous call that must be obeyed."
        imageUri = "https://cards.scryfall.io/normal/front/8/6/86a4c348-1012-4339-960a-c7bc7fd84fbb.jpg"
    }
}
