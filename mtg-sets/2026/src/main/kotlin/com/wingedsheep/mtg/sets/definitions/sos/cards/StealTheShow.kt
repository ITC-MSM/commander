package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveType
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Steal the Show
 * {2}{R}
 * Sorcery
 * Choose one or both —
 * • Target player discards any number of cards, then draws that many cards.
 * • Steal the Show deals damage equal to the number of instant and sorcery cards
 *   in your graveyard to target creature or planeswalker.
 *
 * Modeled as a "choose one or both" modal spell (`chooseCount = 2, minChooseCount = 1`),
 * matching the Karai's Technique / Scour for Scrap idiom. Mode 1 is an inline
 * Gather → Select(any number) → Discard → Draw pipeline scoped to the chosen player
 * (`Chooser.TargetPlayer` makes the discard choice, draw count reads the selected
 * collection's count). Mode 2 reads the controller's graveyard for instant/sorcery
 * cards at resolution via `DynamicAmount.Count`.
 */
val StealTheShow = card("Steal the Show") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Choose one or both —\n" +
        "• Target player discards any number of cards, then draws that many cards.\n" +
        "• Steal the Show deals damage equal to the number of instant and sorcery cards " +
        "in your graveyard to target creature or planeswalker."

    spell {
        modal(chooseCount = 2, minChooseCount = 1) {
            mode("Target player discards any number of cards, then draws that many cards") {
                val player = target("target player", Targets.Player)
                effect = Effects.Pipeline {
                    val hand = gather(CardSource.FromZone(Zone.HAND, Player.ContextPlayer(0)))
                    val discarded = chooseAnyNumber(
                        from = hand,
                        chooser = Chooser.TargetPlayer,
                        prompt = "Choose any number of cards to discard"
                    )
                    move(
                        discarded,
                        CardDestination.ToZone(Zone.GRAVEYARD, Player.ContextPlayer(0)),
                        moveType = MoveType.Discard
                    )
                    run(
                        DrawCardsEffect(
                            DynamicAmount.VariableReference("${discarded.key}_count"),
                            EffectTarget.ContextTarget(0)
                        )
                    )
                }
            }
            mode("Steal the Show deals damage equal to the number of instant and sorcery cards in your graveyard to target creature or planeswalker") {
                val tgt = target("target creature or planeswalker", Targets.CreatureOrPlaneswalker)
                effect = Effects.DealDamage(
                    DynamicAmount.Count(
                        Player.You,
                        Zone.GRAVEYARD,
                        GameObjectFilter.InstantOrSorcery
                    ),
                    tgt
                )
            }
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "130"
        artist = "Pauline Voss"
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7ac6649f-980e-4404-9c05-458c30578ecc.jpg?1775937875"
    }
}
