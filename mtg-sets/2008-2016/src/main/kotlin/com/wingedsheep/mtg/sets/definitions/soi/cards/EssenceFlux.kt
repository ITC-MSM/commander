package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.predicates.CardPredicate

/**
 * Essence Flux
 * {U}
 * Instant
 * Exile target creature you control, then return that card to the battlefield under its owner's
 * control. If it's a Spirit, put a +1/+1 counter on it.
 *
 * The blink is the standard `Move(EXILE) then Move(BATTLEFIELD)` flicker (see Splash Portal /
 * Daydream); the target handle still resolves to the freshly-returned permanent, so the
 * conditional +1/+1 counter reads its subtype after it re-enters.
 */
val EssenceFlux = card("Essence Flux") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Exile target creature you control, then return that card to the battlefield " +
        "under its owner's control. If it's a Spirit, put a +1/+1 counter on it."

    spell {
        val creature = target("creature you control", Targets.CreatureYouControl)
        effect = Effects.Move(creature, Zone.EXILE)
            .then(Effects.Move(creature, Zone.BATTLEFIELD))
            .then(
                ConditionalEffect(
                    condition = Conditions.TargetMatchesFilter(
                        filter = GameObjectFilter(
                            cardPredicates = listOf(CardPredicate.HasSubtype(Subtype("Spirit")))
                        ),
                        targetIndex = 0
                    ),
                    effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
                )
            )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "61"
        artist = "Seb McKinnon"
        flavorText = "A spirit is both fettered and free—bound in some ways to its previous " +
            "existence, yet able to transcend much of the corporeal."
        imageUri = "https://cards.scryfall.io/normal/front/6/3/639bdbb5-8c2d-439d-bcea-dc54da9686ea.jpg?1782712119"
    }
}
