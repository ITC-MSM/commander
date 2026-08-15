package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Restoration Angel
 * {3}{W}
 * Creature — Angel
 * 3/4
 * Flash
 * Flying
 * When this creature enters, you may exile target non-Angel creature you control,
 * then return that card to the battlefield under your control.
 */
val RestorationAngel = card("Restoration Angel") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    power = 3
    toughness = 4
    oracleText = "Flash\nFlying\nWhen this creature enters, you may exile target non-Angel creature you control, then return that card to the battlefield under your control."

    keywords(Keyword.FLASH, Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target(
            "non-Angel creature you control",
            TargetCreature(
                filter = TargetFilter(
                    GameObjectFilter(
                        cardPredicates = listOf(
                            CardPredicate.IsCreature,
                            CardPredicate.NotSubtype(Subtype("Angel"))
                        )
                    ).youControl()
                )
            )
        )
        effect = MayEffect(
            Effects.Move(creature, Zone.EXILE)
                .then(Effects.Move(creature, Zone.BATTLEFIELD))
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "32"
        artist = "Johannes Voss"
        imageUri = "https://cards.scryfall.io/normal/front/c/2/c2ad8639-e586-47f4-baca-2a1af5aa281b.jpg?1782714532"
    }
}
