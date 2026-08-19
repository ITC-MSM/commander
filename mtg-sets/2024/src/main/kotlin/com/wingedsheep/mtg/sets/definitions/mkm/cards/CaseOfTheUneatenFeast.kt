package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.solvedActivatedAbility
import com.wingedsheep.sdk.dsl.toSolve
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.MayCastFromGraveyard
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.ForEachInCollectionEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Case of the Uneaten Feast — Murders at Karlov Manor #10
 * {W} · Enchantment — Case · Rare
 *
 * Whenever a creature you control enters, you gain 1 life.
 * To solve — You've gained 5 or more life this turn.
 * Solved — Sacrifice this Case: Creature cards in your graveyard gain "You may cast this card
 * from your graveyard" until end of turn.
 *
 * "You've gained 5 or more life this turn" is the amount, not the number of gain events, so it is
 * `TurnTracking(You, LIFE_GAINED)` — five separate creatures each gaining 1 solves it, and so does
 * one Lightning Helix. Life *lost* never nets against it.
 *
 * The Solved ability is the interesting half. The printed text grants the permission to *cards*,
 * so the affected set is locked in when the ability resolves (CR 611.2c): a creature card put into
 * the graveyard later that turn is not covered. That rules out modelling it as a standing
 * player-wide "you may cast creature cards from your graveyard this turn" permission anchored to a
 * permanent — which would also have nowhere to anchor, since the Case sacrifices itself as the
 * cost and is gone by the time the ability resolves. So the ability gathers the creature cards in
 * the graveyard and hands each of them its own `MayCastFromGraveyard` grant for the turn; the
 * graveyard-cast enumerator and `CastZoneResolver` read a grant anchored to a graveyard card as
 * that one card's permission.
 *
 * Sacrificing is a cost, so it happens on activation and the ability resolves with the Case
 * already in the graveyard — where, being a creature-less enchantment card, it grants itself
 * nothing.
 */
val CaseOfTheUneatenFeast = card("Case of the Uneaten Feast") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Case"
    oracleText = "Whenever a creature you control enters, you gain 1 life.\n" +
        "To solve — You've gained 5 or more life this turn. (If unsolved, solve at the beginning " +
        "of your end step.)\n" +
        "Solved — Sacrifice this Case: Creature cards in your graveyard gain \"You may cast this " +
        "card from your graveyard\" until end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.youControl(),
            binding = TriggerBinding.ANY
        )
        effect = Effects.GainLife(1)
        description = "Whenever a creature you control enters, you gain 1 life."
    }

    toSolve(Conditions.YouGainedLifeThisTurnAtLeast(5))

    solvedActivatedAbility {
        cost = Costs.SacrificeSelf
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.FromZone(
                    zone = Zone.GRAVEYARD,
                    player = Player.You,
                    filter = GameObjectFilter.Creature
                ),
                storeAs = "uneatenFeast.creatures"
            ),
            ForEachInCollectionEffect(
                collection = "uneatenFeast.creatures",
                effect = Effects.GrantStaticAbility(
                    ability = MayCastFromGraveyard(filter = GameObjectFilter.Creature),
                    target = EffectTarget.Self,
                    duration = Duration.EndOfTurn
                )
            )
        )
        description = "Sacrifice this Case: Creature cards in your graveyard gain \"You may cast " +
            "this card from your graveyard\" until end of turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "10"
        artist = "Titus Lunter"
        imageUri = "https://cards.scryfall.io/normal/front/a/c/ac63941b-3f78-4bd3-8b05-ca12aaaa006c.jpg?1783912926"
    }
}
