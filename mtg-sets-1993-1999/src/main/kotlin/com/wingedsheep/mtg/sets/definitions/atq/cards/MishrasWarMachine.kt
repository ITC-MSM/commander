package com.wingedsheep.mtg.sets.definitions.atq.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DamageRecipient
import com.wingedsheep.sdk.scripting.effects.IfYouDoEffect
import com.wingedsheep.sdk.scripting.effects.PayOrSufferEffect
import com.wingedsheep.sdk.scripting.effects.SuccessCriterion
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Mishra's War Machine
 * {7}
 * Artifact Creature — Juggernaut
 * 5/5
 * Banding
 * At the beginning of your upkeep, this creature deals 3 damage to you unless you discard a card.
 *   If it deals damage to you this way, tap it.
 *
 * Modeling notes:
 * - "deals 3 damage to you unless you discard a card" = [PayOrSufferEffect] (discard to avoid the
 *   damage); the damage's source is the creature itself ([EffectTarget.Self]), so the resulting
 *   `DamageDealtEvent` carries its id.
 * - "If it deals damage to you this way, tap it" = an [IfYouDoEffect] whose
 *   [SuccessCriterion.DamageDealt] (recipient = your controller) gates the tap on damage *actually*
 *   being dealt — if the player discarded, or the damage was prevented/replaced (e.g. a Circle of
 *   Protection), no damage event is emitted and the tap doesn't happen.
 */
val MishrasWarMachine = card("Mishra's War Machine") {
    manaCost = "{7}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Juggernaut"
    power = 5
    toughness = 5
    oracleText = "Banding\n" +
        "At the beginning of your upkeep, this creature deals 3 damage to you unless you discard a card. " +
        "If it deals damage to you this way, tap it."

    keywords(Keyword.BANDING)

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = IfYouDoEffect(
            action = PayOrSufferEffect(
                cost = Costs.pay.Discard(),
                suffer = Effects.DealDamage(
                    amount = 3,
                    target = EffectTarget.Controller,
                    damageSource = EffectTarget.Self
                )
            ),
            ifYouDo = Effects.Tap(EffectTarget.Self),
            successCriterion = SuccessCriterion.DamageDealt(DamageRecipient.Controller)
        )
        description = "At the beginning of your upkeep, this creature deals 3 damage to you unless you " +
            "discard a card. If it deals damage to you this way, tap it."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "57"
        artist = "Amy Weber"
        imageUri = "https://cards.scryfall.io/normal/front/8/f/8f6b4652-a1d4-418f-a89b-6a977a920a9e.jpg?1562925240"
    }
}
