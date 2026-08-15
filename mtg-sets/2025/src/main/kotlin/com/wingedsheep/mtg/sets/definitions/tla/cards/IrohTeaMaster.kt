package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.effects.GiveControlToTargetPlayerEffect
import com.wingedsheep.sdk.scripting.effects.IfYouDoEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.SuccessCriterion
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.predicates.ControllerPredicate
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Iroh, Tea Master
 * {1}{R}{W}
 * Legendary Creature — Human Citizen Ally
 * 2/2
 *
 * When Iroh enters, create a Food token.
 * At the beginning of combat on your turn, you may have target opponent gain control of target
 * permanent you control. When you do, create a 1/1 white Ally creature token. Put a +1/+1 counter
 * on that token for each permanent you own that your opponents control.
 *
 * Modeling notes:
 *  - The ETB Food is the plain [Effects.CreateFood] facade (Canyon Crawler pattern).
 *  - The combat ability declares both targets up front (chosen as the triggered ability is put on
 *    the stack): "target opponent" and "target permanent you control". "you may … When you do" is
 *    the documented `MayEffect(IfYouDoEffect(action, then))` idiom — [MayEffect] gates the optional
 *    "you may", and [IfYouDoEffect] with [SuccessCriterion.ControlChanged] gates the payoff on the
 *    control actually changing (the same deliberate gating Stiltzkin, Moogle Merchant uses for its
 *    "if they do" rider). The `action` is [GiveControlToTargetPlayerEffect] handing the chosen
 *    permanent to the chosen opponent.
 *  - The payoff creates the token and then puts +1/+1 counters on it via the [CREATED_TOKENS]
 *    pipeline (Outlaw Stitcher pattern: [Effects.CreateToken] publishes the new token, then
 *    [Effects.AddDynamicCounters] reads it back as `PipelineTarget(CREATED_TOKENS, 0)`).
 *  - "for each permanent you own that your opponents control" (owner ≠ controller) needs no new
 *    [DynamicAmount]: it composes from the existing aggregate + controller-predicate primitives —
 *    [DynamicAmount.AggregateBattlefield] scoped to permanents an opponent *controls*
 *    ([Player.EachOpponent]) further filtered to those *you own*
 *    ([ControllerPredicate.OwnedByYou], which matches the card's immutable `ownerId` against the
 *    ability controller). The just-given-away permanent — still owned by you, now controlled by the
 *    opponent — is therefore counted.
 */
val IrohTeaMaster = card("Iroh, Tea Master") {
    manaCost = "{1}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Legendary Creature — Human Citizen Ally"
    oracleText = "When Iroh enters, create a Food token.\n" +
        "At the beginning of combat on your turn, you may have target opponent gain control of " +
        "target permanent you control. When you do, create a 1/1 white Ally creature token. Put a " +
        "+1/+1 counter on that token for each permanent you own that your opponents control."
    power = 2
    toughness = 2

    // When Iroh enters, create a Food token.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateFood()
    }

    // At the beginning of combat on your turn, you may have target opponent gain control of target
    // permanent you control. When you do, create a 1/1 white Ally creature token. Put a +1/+1
    // counter on that token for each permanent you own that your opponents control.
    triggeredAbility {
        trigger = Triggers.BeginCombat
        val opponent = target("target opponent", Targets.Opponent)
        val permanent = target(
            "target permanent you control",
            TargetPermanent(filter = TargetFilter.Permanent.youControl())
        )
        effect = MayEffect(
            IfYouDoEffect(
                action = GiveControlToTargetPlayerEffect(
                    permanent = permanent,
                    newController = opponent
                ),
                ifYouDo = Effects.Composite(
                    Effects.CreateToken(
                        power = 1,
                        toughness = 1,
                        colors = setOf(Color.WHITE),
                        creatureTypes = setOf("Ally")
                    ),
                    Effects.AddDynamicCounters(
                        counterType = Counters.PLUS_ONE_PLUS_ONE,
                        amount = DynamicAmount.AggregateBattlefield(
                            player = Player.EachOpponent,
                            filter = GameObjectFilter.Permanent.withControllerPredicate(
                                ControllerPredicate.OwnedByYou
                            )
                        ),
                        target = EffectTarget.PipelineTarget(CREATED_TOKENS, 0)
                    )
                ),
                successCriterion = SuccessCriterion.ControlChanged
            ),
            descriptionOverride = "You may have target opponent gain control of target permanent " +
                "you control. When you do, create a 1/1 white Ally creature token. Put a +1/+1 " +
                "counter on that token for each permanent you own that your opponents control."
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "228"
        artist = "Brian Yuen"
        imageUri = "https://cards.scryfall.io/normal/front/d/1/d1f5e10d-0a89-4129-9717-a921f20d3616.jpg?1764121673"
    }
}
