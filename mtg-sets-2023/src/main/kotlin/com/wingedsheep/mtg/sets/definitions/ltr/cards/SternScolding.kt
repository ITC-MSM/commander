package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetSpell

/**
 * Stern Scolding
 * {U}
 * Instant
 *
 * Counter target creature spell with power or toughness 2 or less.
 */
val SternScolding = card("Stern Scolding") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target creature spell with power or toughness 2 or less."

    spell {
        // creature spell with power or toughness 2 or less
        val filter = TargetFilter(
            GameObjectFilter.Creature.copy(
                cardPredicates = GameObjectFilter.Creature.cardPredicates + CardPredicate.Or(
                    listOf(
                        CardPredicate.PowerAtMost(2),
                        CardPredicate.ToughnessAtMost(2)
                    )
                )
            ),
            zone = Zone.STACK
        )
        target("target creature spell with power or toughness 2 or less", TargetSpell(filter = filter))
        effect = Effects.CounterSpell()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "71"
        artist = "Valera Lutfullina"
        flavorText = "\"Fool of a Took! This is a serious journey, not a Hobbit walking-party. Throw yourself in next time, and then you will be no further nuisance. Now be quiet!\""
        imageUri = "https://cards.scryfall.io/normal/front/3/c/3ca1e1de-b916-445f-b3b2-0f4d0cc7ceeb.jpg?1686968311"
    }
}
