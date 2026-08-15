package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.DelayedTriggerTiming
import com.wingedsheep.sdk.scripting.effects.LoseLifeEffect
import com.wingedsheep.sdk.scripting.effects.PayOrSufferEffect
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Nafs Asp
 * {G}
 * Creature — Snake
 * 1/1
 *
 * "Whenever this creature deals damage to a player, that player loses 1 life at
 * the beginning of their next draw step unless they pay {1} before that draw step."
 *
 * Modelled as: on damage, schedule a one-shot delayed trigger that fires at the
 * start of the damaged player's NEXT draw step. The damaged player is captured
 * via the new [CreateDelayedTriggerEffect.fireOnPlayer] axis, which both gates
 * the firing step to that player's turn and re-exposes them as
 * `Player.TriggeringPlayer` to the inner [PayOrSufferEffect] — so the same
 * player chooses to pay {1} or lose 1 life when the trigger resolves.
 *
 * The 2004 ruling "people commonly pay during upkeep" survives: the player gets
 * a single pay-or-suffer decision once the trigger lands on the stack at the
 * start of their draw step, before that step's turn-based draw.
 */
val NafsAsp = card("Nafs Asp") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Snake"
    power = 1
    toughness = 1
    oracleText = "Whenever this creature deals damage to a player, that player " +
        "loses 1 life at the beginning of their next draw step unless they pay " +
        "{1} before that draw step."

    triggeredAbility {
        trigger = Triggers.dealsDamage(
            damageType = DamageType.Any,
            recipient = RecipientFilter.AnyPlayer
        )
        effect = CreateDelayedTriggerEffect(
            step = Step.DRAW,
            fireOnPlayer = EffectTarget.PlayerRef(Player.TriggeringPlayer),
            timing = DelayedTriggerTiming.CURRENT_TURN_OR_LATER,
            effect = PayOrSufferEffect(
                cost = Costs.pay.Mana(ManaCost.parse("{1}")),
                suffer = LoseLifeEffect(1, EffectTarget.PlayerRef(Player.TriggeringPlayer)),
                player = EffectTarget.PlayerRef(Player.TriggeringPlayer)
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "52"
        artist = "Christopher Rush"
        imageUri = "https://cards.scryfall.io/normal/front/9/6/965f722c-2b18-4c22-8c30-12552def5940.jpg?1562922939"
        ruling(
            "2004-10-04",
            "If its damage gets redirected to its controller, it will still trigger the ability."
        )
        ruling(
            "2004-10-04",
            "If it damages a player twice, they get to pay or take damage twice during their next draw step."
        )
        ruling(
            "2004-10-04",
            "The ability does not cause itself to trigger again. It causes loss of life, not damage."
        )
    }
}
