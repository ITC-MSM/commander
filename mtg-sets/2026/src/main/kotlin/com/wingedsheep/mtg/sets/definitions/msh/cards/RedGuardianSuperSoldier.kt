package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Red Guardian, Super-Soldier
 * {2}{W}
 * Legendary Creature — Human Soldier Villain
 * 2/2
 * Flash
 * When Red Guardian enters, destroy target creature an opponent controls that dealt damage this turn.
 *
 * The target filter is the **active** voice — `TargetFilter.hasDealtDamageThisTurn()`, backed by
 * `StatePredicate.HasDealtDamage(thisTurnOnly = true)`. Its passive near-namesake
 * `wasDealtDamageThisTurn()` (damage *received*, Stingblade Assassin) is a different card entirely.
 *
 * Combat and noncombat damage both qualify, to any recipient — the printed text names no damage type.
 * Flash plus this trigger is the point of the card: hold it up, let a creature connect or a pinger
 * fire, then flash it in and kill the creature. A creature that dealt damage and then left the
 * battlefield and returned no longer qualifies (CR 400.7 — it is a new object). With no legal target
 * the trigger is simply removed from the stack (CR 603.3d); the 2/2 body still arrives.
 */
val RedGuardianSuperSoldier = card("Red Guardian, Super-Soldier") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Soldier Villain"
    oracleText = "Flash\n" +
        "When Red Guardian enters, destroy target creature an opponent controls that dealt damage this turn."
    power = 2
    toughness = 2
    keywords(Keyword.FLASH)
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target(
            "target",
            TargetCreature(filter = TargetFilter.Creature.hasDealtDamageThisTurn().opponentControls())
        )
        effect = Effects.Destroy(t)
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "34"
        artist = "Lee Woo-chul"
        flavorText = "\"You fight for your nation, Captain America. I fight for mine.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/0/c08f8163-319a-4a31-b15a-93974cacd5b7.jpg?1783902967"
    }
}
