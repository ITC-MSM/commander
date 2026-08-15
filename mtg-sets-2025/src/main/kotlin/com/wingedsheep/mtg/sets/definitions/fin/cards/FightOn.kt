package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Fight On!
 * {2}{B}
 * Instant
 * Return up to two target creature cards from your graveyard to your hand.
 */
val FightOn = card("Fight On!") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Return up to two target creature cards from your graveyard to your hand."

    spell {
        target = TargetObject(
            count = 2,
            optional = true,
            filter = TargetFilter.CreatureInYourGraveyard
        )
        effect = ForEachTargetEffect(
            effects = listOf(Effects.Move(EffectTarget.ContextTarget(0), Zone.HAND))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "100"
        artist = "Hokuyuu"
        flavorText = "\"Just hang in there! Someday we'll look back at these hard times and laugh.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/1/e1b0739a-dcc4-4034-96d7-69e551b2f36b.jpg?1748706137"
    }
}
