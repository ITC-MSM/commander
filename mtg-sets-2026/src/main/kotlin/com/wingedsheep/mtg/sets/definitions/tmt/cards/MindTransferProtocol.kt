package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.AddCardTypeEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Mind Transfer Protocol
 * {2}{U}
 * Instant
 *
 * Until end of turn, target artifact or creature becomes an artifact creature with
 * base power and toughness 4/5.
 * Draw a card.
 */
val MindTransferProtocol = card("Mind Transfer Protocol") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Until end of turn, target artifact or creature becomes an artifact creature with base power and toughness 4/5.\nDraw a card."

    spell {
        target = TargetObject(filter = TargetFilter.CreatureOrArtifact)
        // BecomeCreature sets the creature type + base 4/5; AddCardType("ARTIFACT") supplies
        // the "artifact" half so a non-artifact creature target also becomes an artifact.
        effect = Effects.BecomeCreature(
            target = EffectTarget.ContextTarget(0),
            power = 4,
            toughness = 5,
            duration = Duration.EndOfTurn
        )
            .then(AddCardTypeEffect("ARTIFACT", EffectTarget.ContextTarget(0), Duration.EndOfTurn))
            .then(Effects.DrawCards(1))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "45"
        artist = "Chris Seaman"
        flavorText = "\"You will experience some discomfort. Then, for better or worse, the concepts of comfort and discomfort will become quite alien.\"\n—Fugitoid"
        imageUri = "https://cards.scryfall.io/normal/front/2/d/2ddfcc4d-dd5f-42f1-8f33-a8e8b5354534.jpg?1771502582"
    }
}
