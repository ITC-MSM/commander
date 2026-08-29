package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Stampede Rider
 * {2}{R}
 * Creature — Satyr
 * 2/3
 *
 * Trample
 * At the beginning of each combat, if you control a creature with power 4 or greater, this creature
 * gets +1/+1 until end of turn.
 *
 * "each combat" is [Triggers.EachCombat] — `StepEvent(BEGIN_COMBAT, Player.Each)`, the each-turn
 * sibling of Eidolon of Inspiration's `BeginCombat`. The "if …" clause is a true intervening-if
 * (CR 603.4), so it goes in the ability's `interveningIf` field rather than gating the effect;
 * `Conditions.YouControl` is `Exists(You, Battlefield, filter)`, which is the spelling Nessian
 * Hornbeetle in this same set already uses for the identical clause.
 */
val StampedeRider = card("Stampede Rider") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Satyr"
    power = 2
    toughness = 3
    oracleText = "Trample\n" +
        "At the beginning of each combat, if you control a creature with power 4 or greater, " +
        "this creature gets +1/+1 until end of turn."

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.EachCombat
        interveningIf = Conditions.YouControl(GameObjectFilter.Creature.powerAtLeast(4))
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "155"
        artist = "Aaron Miller"
        flavorText = "To a satyr, a stampede is just another kind of revelry."
        imageUri = "https://cards.scryfall.io/normal/front/c/3/c32c6192-2f8d-4656-af8b-c488e27a75e1.jpg"
    }
}
