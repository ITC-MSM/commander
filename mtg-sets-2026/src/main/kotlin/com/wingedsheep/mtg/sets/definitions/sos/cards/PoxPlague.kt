package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ForEachPlayerEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Pox Plague
 * {B}{B}{B}{B}{B}
 * Sorcery
 * Each player loses half their life, then discards half the cards in their hand, then sacrifices
 * half the permanents they control of their choice. Round down each time.
 *
 * A modern Pox/Smallpox. Per the original Pox rulings, the effect is processed in three separate
 * stages, in order — every player loses life, *then* every player discards, *then* every player
 * sacrifices — and the amount for each stage is not calculated until that stage is performed (so an
 * earlier stage's discards/sacrifices change the count for later stages). Each stage is therefore
 * its own [ForEachPlayerEffect] over [Player.ActivePlayerFirst] (APNAP order, CR 101.4), and the
 * three are sequenced with `then`.
 *
 * Within a stage the body's `controllerId` is rebound to the iterated player, so [Player.You] /
 * [EffectTarget.Controller] resolve to that player and the per-player "half, rounded down" amount
 * (`Divide(<count>, Fixed(2), roundUp = false)`) reads *their* own life / hand / permanents:
 * - life loss → `Divide(LifeTotal(You), 2)` to themselves
 * - discard → `Divide(AggregateZone(You, HAND), 2)` (player chooses which cards)
 * - sacrifice → `Divide(AggregateBattlefield(You, Permanent), 2)` permanents "of their choice"
 */
val PoxPlague = card("Pox Plague") {
    manaCost = "{B}{B}{B}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Each player loses half their life, then discards half the cards in their hand, " +
        "then sacrifices half the permanents they control of their choice. Round down each time."

    spell {
        effect = ForEachPlayerEffect(
            Player.ActivePlayerFirst,
            listOf(
                Effects.LoseLife(
                    amount = DynamicAmount.Divide(
                        numerator = DynamicAmount.LifeTotal(Player.You),
                        denominator = DynamicAmount.Fixed(2),
                        roundUp = false,
                    ),
                    target = EffectTarget.Controller,
                ),
            ),
        ).then(
            ForEachPlayerEffect(
                Player.ActivePlayerFirst,
                listOf(
                    Effects.Discard(
                        count = DynamicAmount.Divide(
                            numerator = DynamicAmount.AggregateZone(Player.You, Zone.HAND),
                            denominator = DynamicAmount.Fixed(2),
                            roundUp = false,
                        ),
                        target = EffectTarget.Controller,
                    ),
                ),
            )
        ).then(
            ForEachPlayerEffect(
                Player.ActivePlayerFirst,
                listOf(
                    Effects.Sacrifice(
                        filter = GameObjectFilter.Permanent,
                        count = DynamicAmount.Divide(
                            numerator = DynamicAmount.AggregateBattlefield(
                                Player.You,
                                GameObjectFilter.Permanent,
                            ),
                            denominator = DynamicAmount.Fixed(2),
                            roundUp = false,
                        ),
                        target = EffectTarget.Controller,
                    ),
                ),
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "94"
        artist = "Camille Alquier"
        flavorText = "The darkest corners of the Detention Bog hide natural laboratories that " +
            "quietly spawn deadly infections."
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9c99c17b-ad3a-4859-97e8-469718b81cd9.jpg?1775937566"
    }
}
