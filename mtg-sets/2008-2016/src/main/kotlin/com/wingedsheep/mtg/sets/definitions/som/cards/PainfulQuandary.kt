package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.PayOrSufferEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Painful Quandary
 * {3}{B}{B}
 * Enchantment
 *
 * Whenever an opponent casts a spell, that player loses 5 life unless they discard a card.
 *
 * Modeled as an [Triggers.OpponentCastsSpell] triggered ability whose payoff is a
 * [PayOrSufferEffect]: the affected player is the caster ([Player.TriggeringPlayer]), who may
 * pay the "cost" of discarding a card ([Costs.pay.Discard]) to avoid the suffer — losing 5 life.
 * Both the paying player and the life-loss target are the triggering player, so the punisher
 * always lands on the caster and never on Painful Quandary's controller.
 */
val PainfulQuandary = card("Painful Quandary") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "Whenever an opponent casts a spell, that player loses 5 life unless they discard a card."

    triggeredAbility {
        trigger = Triggers.OpponentCastsSpell
        effect = PayOrSufferEffect(
            cost = Costs.pay.Discard(count = 1),
            suffer = Effects.LoseLife(5, target = EffectTarget.PlayerRef(Player.TriggeringPlayer)),
            player = EffectTarget.PlayerRef(Player.TriggeringPlayer)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "73"
        artist = "Whit Brachna"
        flavorText = "\"For each word spoken, one forgotten. For each thought, a memory rotten.\"\n—Moriok incantation"
        imageUri = "https://cards.scryfall.io/normal/front/f/e/fecf3dae-1a0c-4cf3-b9bd-ec2ad6acaa1b.jpg?1782715326"
    }
}
