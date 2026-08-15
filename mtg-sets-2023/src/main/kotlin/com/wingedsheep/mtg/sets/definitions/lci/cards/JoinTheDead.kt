package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Join the Dead — {1}{B}{B}
 * Instant (common, LCI #110)
 *
 * Target creature gets -5/-5 until end of turn.
 * Descend 4 — That creature gets -10/-10 until end of turn instead if there are four or more
 * permanent cards in your graveyard.
 *
 * The "Descend 4" clause is a resolution-time conditional: [Conditions.CardsInGraveyardMatchingAtLeast]
 * is evaluated when the spell resolves. If true, the -10/-10 branch fires via [ConditionalEffect]
 * (lowers to GatedEffect with Gate.WhenCondition) and the -5/-5 branch is skipped. If false,
 * the -5/-5 branch fires instead. Both branches apply the modifier until end of turn (Layer 7c,
 * per CR 613).
 *
 * Rules note: "instead" in the Descend 4 line means the base -5/-5 does NOT also apply when
 * the condition is met — the two modifiers are mutually exclusive.
 */
val JoinTheDead = card("Join the Dead") {
    manaCost = "{1}{B}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature gets -5/-5 until end of turn.\n" +
        "Descend 4 — That creature gets -10/-10 until end of turn instead if there are four or more permanent cards in your graveyard."

    spell {
        val t = target("target creature", TargetCreature())
        effect = ConditionalEffect(
            condition = Conditions.CardsInGraveyardMatchingAtLeast(4, GameObjectFilter.Permanent),
            effect = Effects.ModifyStats(-10, -10, t),
            elseEffect = Effects.ModifyStats(-5, -5, t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "110"
        artist = "Olivier Bernard"
        flavorText = "The more creatures that plunge to their deaths in the abyss, the more angry Echoes that arise to drag others down."
        imageUri = "https://cards.scryfall.io/normal/front/b/5/b5bf6c25-a7d7-40b6-aa19-f852d348967f.jpg?1782694524"
    }
}
