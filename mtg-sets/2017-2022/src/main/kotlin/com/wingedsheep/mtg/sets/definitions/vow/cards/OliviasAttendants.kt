package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Olivia's Attendants
 * {4}{R}{R}
 * Creature — Vampire
 * 6/6
 *
 * Menace
 * Whenever this creature deals damage, create that many Blood tokens.
 * {2}{R}: This creature deals 1 damage to any target.
 *
 * The trigger is the *bare* [Triggers.DealsDamage] — any damage, of any type, to any recipient.
 * That is wider than the usual combat-damage trigger on purpose: the Attendants' own pinger feeds
 * it, and so does a blocked creature's damage to a blocker, or damage a fight effect makes it deal.
 *
 * "That many" is the amount from the triggering damage event,
 * [ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT], which is why the count is a `DynamicAmount` rather
 * than a fixed number — a 6/6 hitting a player makes six Blood, the pinger's ping makes one, and a
 * damage-doubling effect is read off the event rather than recomputed from the creature's power.
 */
val OliviasAttendants = card("Olivia's Attendants") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Vampire"
    power = 6
    toughness = 6
    oracleText = "Menace\n" +
        "Whenever this creature deals damage, create that many Blood tokens. (They're artifacts " +
        "with \"{1}, {T}, Discard a card, Sacrifice this token: Draw a card.\")\n" +
        "{2}{R}: This creature deals 1 damage to any target."

    keywords(Keyword.MENACE)

    triggeredAbility {
        trigger = Triggers.DealsDamage
        effect = Effects.CreateBlood(
            DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT)
        )
        description = "Whenever this creature deals damage, create that many Blood tokens."
    }

    activatedAbility {
        cost = Costs.Mana("{2}{R}")
        val anyTarget = target("any target", Targets.Any)
        effect = Effects.DealDamage(1, anyTarget)
        description = "This creature deals 1 damage to any target."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "172"
        artist = "Dmitry Burmak"
        imageUri = "https://cards.scryfall.io/normal/front/d/9/d9702fa3-9323-4e2f-92c9-6c31df198af2.jpg?1783924826"
    }
}
