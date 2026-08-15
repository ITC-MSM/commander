package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.exploit
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Graf Reaver
 * {1}{B}
 * Creature — Zombie Warrior
 * 3/3
 * Exploit (When this creature enters, you may sacrifice a creature.)
 * When this creature exploits a creature, destroy target planeswalker.
 * At the beginning of your upkeep, this creature deals 1 damage to you.
 *
 * Two independent pieces beyond the exploit sacrifice:
 *  - The exploit payoff targets a planeswalker ([exploit]'s `onExploitTargets` = [Targets.Planeswalker]),
 *    chosen after the sacrifice resolves; the reflexive is only put on the stack when a legal
 *    planeswalker exists, so exploiting with no planeswalker in play just performs the sacrifice.
 *  - A [Triggers.YourUpkeep] drawback that deals 1 damage to you (the controller), mirroring
 *    Ravenous Giant.
 */
val GrafReaver = card("Graf Reaver") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Warrior"
    power = 3
    toughness = 3
    oracleText = "Exploit (When this creature enters, you may sacrifice a creature.)\n" +
        "When this creature exploits a creature, destroy target planeswalker.\n" +
        "At the beginning of your upkeep, this creature deals 1 damage to you."

    exploit(
        onExploit = Effects.Destroy(EffectTarget.ContextTarget(0)),
        onExploitTargets = listOf(Targets.Planeswalker)
    )

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.DealDamage(1, EffectTarget.PlayerRef(Player.You))
        description = "At the beginning of your upkeep, this creature deals 1 damage to you."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "115"
        artist = "Dave Kendall"
        imageUri = "https://cards.scryfall.io/normal/front/0/b/0bb2103b-3462-4439-a1e1-243a15c5f318.jpg?1783924863"
    }
}
