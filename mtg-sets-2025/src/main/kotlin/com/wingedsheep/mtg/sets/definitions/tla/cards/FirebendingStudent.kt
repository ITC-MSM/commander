package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.effects.ManaExpiry
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Firebending Student
 * {1}{R}
 * Creature — Human Monk
 * 1/2
 *
 * Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)
 * Firebending X, where X is this creature's power. (Whenever this creature attacks, add X {R}.
 * This mana lasts until end of combat.)
 *
 * Dynamic firebending follows the [com.wingedsheep.mtg.sets.definitions.tla.cards.FireLordZuko]
 * pattern: the fixed `firebending(n)` DSL only models a constant N, so the attack trigger is
 * hand-wired as an [AddManaEffect] producing red mana equal to this creature's (projected) power
 * with [ManaExpiry.END_OF_COMBAT]. Reading projected power means prowess's +1/+1 buffs are
 * already reflected in X when the trigger resolves. The display keyword ability is omitted because
 * the keyword is fixed-N only; the reminder text lives in `oracleText`.
 */
val FirebendingStudent = card("Firebending Student") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Monk"
    power = 1
    toughness = 2
    oracleText = "Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)\n" +
        "Firebending X, where X is this creature's power. (Whenever this creature attacks, add X {R}. " +
        "This mana lasts until end of combat.)"

    prowess()

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = AddManaEffect(
            Color.RED,
            DynamicAmount.EntityProperty(EntityReference.Source, EntityNumericProperty.Power),
            expiry = ManaExpiry.END_OF_COMBAT,
        )
        description = "Firebending X, where X is this creature's power. Whenever this creature " +
            "attacks, add X {R}. This mana lasts until end of combat."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "139"
        artist = "Kozato"
        flavorText = "\"Fire is the element of power.\"\n—Iroh"
        imageUri = "https://cards.scryfall.io/normal/front/3/b/3b366f59-16fe-43cb-888d-1f93ef8fd332.jpg?1764120954"
    }
}
