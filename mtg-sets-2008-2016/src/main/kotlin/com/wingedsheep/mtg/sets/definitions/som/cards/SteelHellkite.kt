package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Steel Hellkite
 * {6}
 * Artifact Creature — Dragon
 * 5/5
 *
 * Flying
 * {2}: This creature gets +1/+0 until end of turn.
 * {X}: Destroy each nonland permanent with mana value X whose controller was dealt combat damage
 * by this creature this turn. Activate only once each turn.
 *
 * - The {X} ability pairs two filter predicates: `manaValueEqualsX()` reads the X paid for this
 *   activation (so X=0 destroys mana-value-0 permanents, including tokens — CR 202.3b), and
 *   `controllerDealtCombatDamageBySourceThisTurn()` restricts the sweep to permanents controlled
 *   by players this Hellkite connected with this turn, read off its own per-turn recipient marker.
 * - Both checks happen on resolution against the *current* board (CR 608.2): it doesn't matter who
 *   controlled a permanent when the damage was dealt, or whether it was on the battlefield then.
 * - "Activate only once each turn" is [ActivationRestriction.OncePerTurn]; the ability is legal to
 *   activate even when the Hellkite hasn't connected with anyone, it just destroys nothing.
 */
val SteelHellkite = card("Steel Hellkite") {
    manaCost = "{6}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Dragon"
    power = 5
    toughness = 5
    oracleText = "Flying\n" +
        "{2}: This creature gets +1/+0 until end of turn.\n" +
        "{X}: Destroy each nonland permanent with mana value X whose controller was dealt combat " +
        "damage by this creature this turn. Activate only once each turn."

    keywords(Keyword.FLYING)

    // {2}: This creature gets +1/+0 until end of turn.
    activatedAbility {
        cost = Costs.Mana("{2}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    // {X}: Destroy each nonland permanent with mana value X whose controller was dealt combat
    // damage by this creature this turn. Activate only once each turn.
    activatedAbility {
        cost = Costs.Mana("{X}")
        restrictions = listOf(ActivationRestriction.OncePerTurn)
        effect = Effects.DestroyAll(
            filter = GameObjectFilter.NonlandPermanent
                .manaValueEqualsX()
                .controllerDealtCombatDamageBySourceThisTurn()
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "205"
        artist = "James Paick"
        imageUri = "https://cards.scryfall.io/normal/front/b/1/b126ee24-9597-4ee8-9c4d-5caed585424a.jpg?1783941698"
    }
}
