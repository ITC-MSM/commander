package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sparring Regimen — Strixhaven: School of Mages #29 (canonical printing)
 * {2}{W} · Enchantment
 *
 * When this enchantment enters, learn.
 * Whenever you attack, put a +1/+1 counter on target attacking creature and untap it.
 *
 * "Whenever you attack" is [Triggers.YouAttack] — a player-level trigger that fires once per
 * combat when attackers are declared (CR 508.1), *not* once per attacker. The target is chosen
 * when the ability goes on the stack, by which time attackers are declared, so
 * [Targets.AttackingCreature] always has a legal choice when the trigger fires at all.
 *
 * Untapping the attacker does not remove it from combat (CR 506.4) — it stays an attacking
 * creature and still deals combat damage; the untap is a pseudo-vigilance rider.
 *
 * `Learn` is [Patterns.Mechanic.learn] (CR 701.48).
 */
val SparringRegimen = card("Sparring Regimen") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, learn. (You may reveal a Lesson card you own from " +
        "outside the game and put it into your hand, or discard a card to draw a card.)\n" +
        "Whenever you attack, put a +1/+1 counter on target attacking creature and untap it."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Mechanic.learn()
    }

    triggeredAbility {
        trigger = Triggers.YouAttack
        val attacker = target("target attacking creature", Targets.AttackingCreature)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, attacker) then
            Effects.Untap(attacker)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "29"
        artist = "Tomasz Jedruszek"
        imageUri = "https://cards.scryfall.io/normal/front/a/b/ab7c80d3-3e0c-4510-9ed0-bb9fe39d838f.jpg?1783927388"
    }
}
