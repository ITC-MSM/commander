package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Cutthroat Maneuver
 * {3}{B}
 * Instant
 *
 * Up to two target creatures each get +1/+1 and gain lifelink until end of turn.
 *
 * "Up to two target creatures **each** get ..." is a plural requirement, so the pump and the
 * keyword grant run once per chosen target ([ForEachTargetEffect] over `ContextTarget(0)`)
 * rather than once against the requirement as a whole.
 */
val CutthroatManeuver = card("Cutthroat Maneuver") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Up to two target creatures each get +1/+1 and gain lifelink until end of turn."

    spell {
        target("target", Targets.UpToCreatures(2))
        effect = ForEachTargetEffect(
            listOf(
                Effects.ModifyStats(1, 1, EffectTarget.ContextTarget(0)),
                Effects.GrantKeyword(Keyword.LIFELINK, EffectTarget.ContextTarget(0)),
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "82"
        artist = "Brad Rigney"
        flavorText = "\"Our ambition drives us forward. Together we will claim what is ours, no matter who holds it.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/2/62497880-64dc-4911-8513-f95b73086136.jpg"
    }
}
