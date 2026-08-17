package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardLayout
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.TurnFaceUpEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Hustle // Bustle — Murders at Karlov Manor #249
 *
 * Hustle creates both combat requirements on its one target; legality still excuses a creature
 * that cannot attack or block. Bustle pumps the current creature group, then uses a non-targeting
 * battlefield selection for its optional face-up action, so ward, hexproof, and shroud do not
 * interfere with that choice.
 */
val HustleBustle = card("Hustle // Bustle") {
    layout = CardLayout.SPLIT
    colorIdentity = "URG"

    face("Hustle") {
        manaCost = "{U/R}"
        typeLine = "Instant"
        oracleText = "Target creature attacks or blocks this turn if able."

        spell {
            val creature = target("target creature", com.wingedsheep.sdk.dsl.Targets.Creature)
            effect = Effects.Composite(
                Effects.MarkMustAttackThisTurn(creature),
                Effects.MarkMustBlockThisTurn(creature),
            )
        }
    }

    face("Bustle") {
        manaCost = "{4}{R/G}{R/G}"
        typeLine = "Sorcery"
        oracleText = "Creatures you control get +2/+2 and gain trample until end of turn. You may " +
            "turn a creature you control face up."

        spell {
            effect = Effects.Composite(
                Effects.ForEachInGroup(
                    GroupFilter(GameObjectFilter.Creature.youControl()),
                    Effects.Composite(
                        Effects.ModifyStats(2, 2, EffectTarget.Self),
                        Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.Self),
                    ),
                ),
                GatherCardsEffect(
                    source = CardSource.BattlefieldMatching(
                        filter = GameObjectFilter.Creature.faceDown(),
                        player = Player.You,
                    ),
                    storeAs = "faceDownCreatures",
                ),
                SelectFromCollectionEffect(
                    from = "faceDownCreatures",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                    storeSelected = "turnFaceUp",
                    prompt = "You may turn a creature you control face up",
                    useTargetingUI = true,
                ),
                TurnFaceUpEffect(EffectTarget.PipelineTarget("turnFaceUp", 0)),
            )
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "249"
        artist = "Valera Lutfullina"
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5f664827-e22e-43af-82f1-861b3c7607f1.jpg?1783912830"

        ruling(
            "2024-02-02",
            "A creature that is tapped, cannot attack or block, or would require an unpaid cost " +
                "is not forced to attack or block.",
        )
    }
}
