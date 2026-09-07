package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.transmute
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Shred Memory
 * {1}{B}
 * Instant
 * Exile up to four target cards from a single graveyard.
 * Transmute {1}{B}{B}
 *
 * "From a single graveyard" is the cross-target [TargetObject.sameOwner] constraint (Arashin
 * Sunshield / Qutrub Forayer shape) — every chosen card must share an owner, checked by
 * `TargetValidator` at cast time. "Up to four" is `count = 4, optional = true`, so zero is a legal
 * choice and the spell still resolves. The payoff is a [ForEachTargetEffect] over
 * [EffectTarget.ContextTarget], since a multi-slot target requirement binds no single handle.
 */
val ShredMemory = card("Shred Memory") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Exile up to four target cards from a single graveyard.\n" +
        "Transmute {1}{B}{B} ({1}{B}{B}, Discard this card: Search your library for a card with the same mana value as this card, reveal it, put it into your hand, then shuffle. Transmute only as a sorcery.)"

    spell {
        target(
            "up to four target cards from a single graveyard",
            TargetObject(
                count = 4,
                optional = true,
                filter = TargetFilter.CardInGraveyard,
                sameOwner = true,
            ),
        )
        effect = ForEachTargetEffect(
            effects = listOf(Effects.Move(EffectTarget.ContextTarget(0), Zone.EXILE))
        )
    }

    transmute("{1}{B}{B}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "105"
        artist = "Hideaki Takamura"
        imageUri = "https://cards.scryfall.io/normal/front/e/3/e38192e5-814f-4269-bae8-13867a73e7fa.jpg?1783943663"
    }
}
