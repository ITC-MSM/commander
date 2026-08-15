package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.DelayedTriggerExpiry
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sandals of Abdallah
 * {4}
 * Artifact
 * {2}, {T}: Target creature gains islandwalk until end of turn. When that creature dies
 * this turn, destroy this artifact.
 */
val SandalsOfAbdallah = card("Sandals of Abdallah") {
    manaCost = "{4}"
    typeLine = "Artifact"
    oracleText = "{2}, {T}: Target creature gains islandwalk until end of turn. When that creature dies this turn, destroy this artifact. (A creature with islandwalk can't be blocked as long as defending player controls an Island.)"

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap)
        val creature = target("target creature", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.ISLANDWALK, creature, Duration.EndOfTurn).then(
            CreateDelayedTriggerEffect(
                trigger = Triggers.Dies,
                watchedTarget = creature,
                expiry = DelayedTriggerExpiry.EndOfTurn,
                effect = Effects.Destroy(EffectTarget.Self),
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "69"
        artist = "Dan Frazier"
        imageUri = "https://cards.scryfall.io/normal/front/8/f/8f99a520-b8a9-40b0-9854-48aac297c5ee.jpg?1562921667"
    }
}
