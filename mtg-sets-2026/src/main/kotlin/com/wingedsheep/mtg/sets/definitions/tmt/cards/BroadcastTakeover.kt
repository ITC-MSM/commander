package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.GainControlEffect
import com.wingedsheep.sdk.scripting.effects.GrantKeywordEffect
import com.wingedsheep.sdk.scripting.effects.TapUntapEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Broadcast Takeover
 * {2}{R}{R}{R}
 * Sorcery
 *
 * Gain control of all artifacts your opponents control until end of turn. Untap
 * them. They gain haste until end of turn.
 */
val BroadcastTakeover = card("Broadcast Takeover") {
    manaCost = "{2}{R}{R}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Gain control of all artifacts your opponents control until end of turn. Untap them. They gain haste until end of turn."

    // Untap + haste run while the artifacts are still opponent-controlled (the
    // GroupFilter re-evaluates per ForEachInGroup), so gain-control comes last; the
    // UEOT untap/haste persist on the same objects after control changes (Insurrection shape).
    val opponentArtifacts = GroupFilter(GameObjectFilter.Artifact.opponentControls())

    spell {
        effect = Effects.Composite(
            Effects.ForEachInGroup(opponentArtifacts, TapUntapEffect(EffectTarget.Self, tap = false)),
            Effects.ForEachInGroup(opponentArtifacts, GrantKeywordEffect(Keyword.HASTE, EffectTarget.Self, Duration.EndOfTurn)),
            Effects.ForEachInGroup(opponentArtifacts, GainControlEffect(EffectTarget.Self, Duration.EndOfTurn))
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "86"
        artist = "Hokyoung Kim"
        flavorText = "The power of the Foot is its ability to infiltrate every system from the shadows."
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e640e930-0907-40f2-9015-88f8c1e8ee5c.jpg?1769005852"
    }
}
