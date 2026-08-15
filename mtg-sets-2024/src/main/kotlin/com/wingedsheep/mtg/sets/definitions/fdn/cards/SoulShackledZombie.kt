package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.ConditionalOnCollectionEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Soul-Shackled Zombie
 * {3}{B}
 * Creature — Zombie
 * 4/2
 *
 * When this creature enters, exile up to two target cards from a single graveyard. If at least
 * one creature card was exiled this way, each opponent loses 2 life and you gain 2 life.
 *
 * "From a single graveyard" is the cross-target [TargetObject.sameOwner] constraint (Arashin
 * Sunshield shape). The chosen cards are gathered ([CardSource.ChosenTargets]) so the post-exile
 * "if at least one creature card was exiled this way" check is a [ConditionalOnCollectionEffect]
 * over the exiled pile filtered to creatures — gating the drain on what actually moved, not on
 * the (possibly-fizzled) targets.
 */
val SoulShackledZombie = card("Soul-Shackled Zombie") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie"
    power = 4
    toughness = 2
    oracleText = "When this creature enters, exile up to two target cards from a single graveyard. " +
        "If at least one creature card was exiled this way, each opponent loses 2 life and you " +
        "gain 2 life."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target(
            "up to two target cards from a single graveyard",
            TargetObject(
                count = 2,
                optional = true,
                filter = TargetFilter.CardInGraveyard,
                sameOwner = true
            )
        )
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.ChosenTargets,
                storeAs = "ssz_exiled"
            ),
            MoveCollectionEffect(
                from = "ssz_exiled",
                destination = CardDestination.ToZone(Zone.EXILE)
            ),
            ConditionalOnCollectionEffect(
                collection = "ssz_exiled",
                filter = GameObjectFilter.Creature,
                ifNotEmpty = Effects.Composite(
                    Effects.LoseLife(2, EffectTarget.PlayerRef(Player.EachOpponent)),
                    Effects.GainLife(2)
                ),
                ifEmpty = Effects.Composite(emptyList())
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "70"
        artist = "Diana Franco"
        flavorText = "The ritual successfully rebound her soul to her body, but not quite in the way she'd planned."
        imageUri = "https://cards.scryfall.io/normal/front/d/e/deea5690-6eb2-4353-b917-cbbf840e4e71.jpg?1782689206"
    }
}
