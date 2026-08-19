package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Surge of Thoughtweft
 * {1}{W}
 * Kindred Instant — Kithkin
 * Creatures you control get +1/+1 until end of turn. If you control a Kithkin, draw a card.
 */
val SurgeOfThoughtweft = card("Surge of Thoughtweft") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Kindred Instant — Kithkin"
    oracleText = "Creatures you control get +1/+1 until end of turn. If you control a Kithkin, draw a card."

    spell {
        effect = Effects.Composite(
            Effects.ForEachInGroup(
                GroupFilter(GameObjectFilter.Creature.youControl()),
                Effects.ModifyStats(1, 1, EffectTarget.Self)
            ),
            ConditionalEffect(
                condition = Conditions.ControlPermanentOfType(Subtype.KITHKIN),
                effect = Effects.DrawCards(1)
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "43"
        artist = "Randy Gallegos"
        flavorText = "Kithkin weave together their very thoughts, creating a depth of cooperation unknown to other races."
        imageUri = "https://cards.scryfall.io/normal/front/e/0/e0b61fb2-11e5-45ae-873d-85f79f161950.jpg?1783942908"
    }
}
