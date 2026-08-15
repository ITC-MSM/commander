package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Supertype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Ember Island Production
 * {3}{U}{U}
 * Sorcery
 * Choose one —
 * • Create a token that's a copy of target creature you control, except it's not legendary
 *   and it's a 4/4 Hero in addition to its other types.
 * • Create a token that's a copy of target creature an opponent controls, except it's not
 *   legendary and it's a 2/2 Coward in addition to its other types.
 *
 * A "Choose one —" modal spell ([ModalEffect.chooseOne]). Each mode creates a token copy of a
 * targeted creature with the legendary supertype stripped, base P/T overridden, and a creature
 * subtype unioned onto its other types (Hero for your creature, Coward for an opponent's).
 */
val EmberIslandProduction = card("Ember Island Production") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Choose one —\n" +
        "• Create a token that's a copy of target creature you control, except it's not legendary and it's a 4/4 Hero in addition to its other types.\n" +
        "• Create a token that's a copy of target creature an opponent controls, except it's not legendary and it's a 2/2 Coward in addition to its other types."

    spell {
        effect = ModalEffect.chooseOne(
            Mode(
                effect = Effects.CreateTokenCopyOfTarget(
                    target = EffectTarget.ContextTarget(0),
                    overridePower = 4,
                    overrideToughness = 4,
                    removedSupertypes = setOf(Supertype.LEGENDARY),
                    addedSubtypes = setOf(Subtype("Hero")),
                ),
                targetRequirements = listOf(TargetObject(filter = TargetFilter.CreatureYouControl)),
                description = "Create a token that's a copy of target creature you control, except it's not legendary and it's a 4/4 Hero in addition to its other types",
            ),
            Mode(
                effect = Effects.CreateTokenCopyOfTarget(
                    target = EffectTarget.ContextTarget(0),
                    overridePower = 2,
                    overrideToughness = 2,
                    removedSupertypes = setOf(Supertype.LEGENDARY),
                    addedSubtypes = setOf(Subtype("Coward")),
                ),
                targetRequirements = listOf(TargetObject(filter = TargetFilter.CreatureOpponentControls)),
                description = "Create a token that's a copy of target creature an opponent controls, except it's not legendary and it's a 2/2 Coward in addition to its other types",
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "48"
        artist = "Brian Yuen"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0f79a7fc-36b1-4397-8e67-6638379d3a38.jpg?1775728768"
    }
}
