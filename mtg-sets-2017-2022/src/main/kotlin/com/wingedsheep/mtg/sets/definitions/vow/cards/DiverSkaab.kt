package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.exploit
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Diver Skaab
 * {3}{U}{U}
 * Creature — Zombie
 * 3/5
 * Exploit (When this creature enters, you may sacrifice a creature.)
 * When this creature exploits a creature, target creature's owner puts it on their choice of the
 * top or bottom of their library.
 *
 * The payoff targets a creature, so it rides the exploit reflexive's target requirement
 * ([exploit]'s `onExploitTargets`); the target is chosen **after** the sacrifice resolves.
 * [Effects.PutOnTopOrBottomOfLibrary] pauses for the targeted creature's **owner** to choose top
 * or bottom, matching the oracle wording exactly.
 */
val DiverSkaab = card("Diver Skaab") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Zombie"
    power = 3
    toughness = 5
    oracleText = "Exploit (When this creature enters, you may sacrifice a creature.)\n" +
        "When this creature exploits a creature, target creature's owner puts it on their choice " +
        "of the top or bottom of their library."

    exploit(
        onExploit = Effects.PutOnTopOrBottomOfLibrary(EffectTarget.ContextTarget(0)),
        onExploitTargets = listOf(Targets.Creature)
    )

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "56"
        artist = "Dave Kendall"
        imageUri = "https://cards.scryfall.io/normal/front/3/8/38b5fdf4-3884-436f-8066-bc7593e72b02.jpg?1783924897"
    }
}
