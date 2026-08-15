package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Dream Spoilers
 * {3}{B}
 * Creature — Faerie Warlock
 * 2/2
 *
 * Flying
 * Whenever you cast a spell during an opponent's turn, up to one target creature an opponent
 * controls gets -1/-1 until end of turn.
 *
 * "During an opponent's turn" is checked when the ability would trigger, so it rides on
 * `triggerCondition` ([Conditions.IsNotYourTurn]) rather than gating the resolved effect —
 * only players take turns, so "not your turn" is exactly "an opponent's turn".
 * "Up to one target" is an optional target, so the ability still resolves (doing nothing)
 * when no creature is chosen or the chosen one has left.
 */
val DreamSpoilers = card("Dream Spoilers") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Faerie Warlock"
    oracleText = "Flying\n" +
        "Whenever you cast a spell during an opponent's turn, up to one target creature an " +
        "opponent controls gets -1/-1 until end of turn."
    power = 2
    toughness = 2

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YouCastSpell
        triggerCondition = Conditions.IsNotYourTurn
        val t = target(
            "target",
            TargetCreature(optional = true, filter = TargetFilter.Creature.opponentControls())
        )
        effect = Effects.ModifyStats(power = -1, toughness = -1, target = t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "85"
        artist = "Jodie Muir"
        flavorText = "The Wicked Slumber saved Eldraine from Phyrexia's grasp, but instead of " +
            "fading when the invasion ended, it continued to spread."
        imageUri = "https://cards.scryfall.io/normal/front/4/e/4efd1963-fe71-42c1-8ad7-53fd80145ca6.jpg?1783915109"
    }
}
