package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Jet's Brainwashing — {R}
 * Sorcery
 * Kicker {3} (You may pay an additional {3} as you cast this spell.)
 * Target creature can't block this turn. If this spell was kicked, gain control of
 * that creature until end of turn, untap it, and it gains haste until end of turn.
 * Create a Clue token. (It's an artifact with "{2}, Sacrifice this token: Draw a card.")
 */
val JetsBrainwashing = card("Jet's Brainwashing") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Kicker {3} (You may pay an additional {3} as you cast this spell.)\n" +
        "Target creature can't block this turn. If this spell was kicked, gain control of " +
        "that creature until end of turn, untap it, and it gains haste until end of turn.\n" +
        "Create a Clue token. (It's an artifact with \"{2}, Sacrifice this token: Draw a card.\")"

    keywordAbility(KeywordAbility.kicker("{3}"))

    spell {
        val t = target("target creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.CantBlock(t),
            ConditionalEffect(
                condition = WasKicked,
                effect = Effects.Composite(
                    Effects.GainControl(t, Duration.EndOfTurn),
                    Effects.Untap(t),
                    Effects.GrantKeyword(Keyword.HASTE, t),
                ),
            ),
            Effects.CreateClue(),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "143"
        artist = "Enishi"
        imageUri = "https://cards.scryfall.io/normal/front/b/1/b17e8bdb-4b91-4a9d-bfe1-8a55f0bd040b.jpg?1764120981"
    }
}
