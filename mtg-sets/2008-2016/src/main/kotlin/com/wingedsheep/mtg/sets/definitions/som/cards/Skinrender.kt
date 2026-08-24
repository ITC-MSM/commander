package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Skinrender — Scars of Mirrodin #78
 * {2}{B}{B} · Creature — Phyrexian Zombie · 3 / 3
 *
 * When this creature enters, put three -1/-1 counters on target creature.
 *
 * The trigger is not optional and the target is not "another creature" — with no other creature on
 * the battlefield the Skinrender must target itself and shrinks to 0/0.
 */
val Skinrender = card("Skinrender") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Phyrexian Zombie"
    power = 3
    toughness = 3
    oracleText = "When this creature enters, put three -1/-1 counters on target creature."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", Targets.Creature)
        effect = Effects.AddCounters(Counters.MINUS_ONE_MINUS_ONE, 3, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "78"
        artist = "David Rapoza"
        flavorText = "\"Your creations are effective, Sheoldred, but we must unite the flesh, not merely flay it.\"\n—Elesh Norn, Grand Cenobite"
        imageUri = "https://cards.scryfall.io/normal/front/b/e/be358357-2abe-4ead-bb18-76cad8274489.jpg?1783941728"
    }
}
