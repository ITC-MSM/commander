package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Applied Geometry
 * {2}{G}{U}
 * Sorcery
 *
 * Create a token that's a copy of target non-Aura permanent you control, except it's a 0/0 Fractal
 * creature in addition to its other types. Put six +1/+1 counters on it.
 *
 * Composed from existing atoms: [Effects.CreateTokenCopyOfTarget] makes the copy and applies the
 * "except it's a 0/0 Fractal creature in addition to its other types" rider —
 * `overridePower/Toughness = 0`, `addCardTypes = {CREATURE}` (unioned on top of the copied types,
 * so an artifact/enchantment copy stays one too — CR 707.9b "in addition to"), and
 * `addedSubtypes = {Fractal}`. The executor publishes the created token under the well-known
 * [CREATED_TOKENS] pipeline collection, so the following
 * [Effects.AddCountersToCollection] puts the six +1/+1 counters on exactly the token just created —
 * no bespoke "copy then counter" effect needed.
 *
 * "non-Aura permanent you control" is expressed by composing `notSubtype(Aura)` onto
 * `GameObjectFilter.Permanent.youControl()` (Auras are the only permanent type that can't legally
 * be copied as a standalone object here).
 */
val AppliedGeometry = card("Applied Geometry") {
    manaCost = "{2}{G}{U}"
    colorIdentity = "UG"
    typeLine = "Sorcery"
    oracleText = "Create a token that's a copy of target non-Aura permanent you control, except " +
        "it's a 0/0 Fractal creature in addition to its other types. Put six +1/+1 counters on it."

    spell {
        val copyTarget = target(
            "target non-Aura permanent you control",
            TargetObject(
                filter = TargetFilter(
                    GameObjectFilter.Permanent.youControl().notSubtype(Subtype("Aura")),
                ),
            ),
        )
        effect = Effects.Composite(
            Effects.CreateTokenCopyOfTarget(
                target = copyTarget,
                overridePower = 0,
                overrideToughness = 0,
                addCardTypes = setOf("CREATURE"),
                addedSubtypes = setOf(Subtype.FRACTAL),
            ),
            Effects.AddCountersToCollection(CREATED_TOKENS, Counters.PLUS_ONE_PLUS_ONE, 6),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "172"
        artist = "Justyna Dura"
        flavorText = "\"I know the prompt was to replicate a scurrid, but I took some creative " +
            "liberties with my version.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/1/f109f2eb-895b-44a6-b6b5-81bf3831ccd5.jpg?1775938180"
    }
}
