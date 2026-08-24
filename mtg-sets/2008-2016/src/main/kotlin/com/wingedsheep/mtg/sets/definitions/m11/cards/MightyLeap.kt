package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Mighty Leap
 * {1}{W}
 * Instant
 * Target creature gets +2/+2 and gains flying until end of turn.
 *
 * The ordinary pump-and-grant pair over one named target — both halves default to
 * `Duration.EndOfTurn`, which is what the printed "until end of turn" says once for both.
 */
val MightyLeap = card("Mighty Leap") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Target creature gets +2/+2 and gains flying until end of turn."

    spell {
        val creature = target("target", Targets.Creature)
        effect = Effects.ModifyStats(2, 2, creature) then
            Effects.GrantKeyword(Keyword.FLYING, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "22"
        artist = "rk post"
        flavorText = "\"The southern fortress taken by invaders? Heh, sure . . . when elephants fly.\"\n" +
            "—Brezard Skeinbow, captain of the guard"
        imageUri = "https://cards.scryfall.io/normal/front/b/f/bf8e0f93-a450-4188-a735-d601a59ab108.jpg"
    }
}
