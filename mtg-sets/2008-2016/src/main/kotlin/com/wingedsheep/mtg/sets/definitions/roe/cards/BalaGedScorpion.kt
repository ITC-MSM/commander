package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bala Ged Scorpion
 * {3}{B}
 * Creature — Scorpion
 * 2 / 3
 *
 * When this creature enters, you may destroy target creature with power 1 or less.
 *
 * Modeling notes:
 *  - Both halves of "you may destroy **target**" have to be present, and they happen at different
 *    times: the target is chosen when the trigger goes on the stack (so it is a `target(...)`
 *    handle on the ability, and CR 608.2b re-checks "power 1 or less" on resolution — pumping the
 *    creature in response fizzles the ability), while the "may" is a decline offered *on*
 *    resolution. `optional = true` is the authoring shorthand that lowers the effect into a
 *    `Gate.MayDecide`, which is exactly the `Gated` wrapper Assay compiles from this line.
 *  - "Creature with power 1 or less" is any creature on the battlefield — no "you control" is
 *    printed — so it is the unscoped [Targets.CreatureWithPowerAtMost] facade.
 *  - Plain [Effects.Destroy] (regeneration still allowed): the card says "destroy", not "destroy …
 *    it can't be regenerated".
 */
val BalaGedScorpion = card("Bala Ged Scorpion") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Scorpion"
    power = 2
    toughness = 3
    oracleText = "When this creature enters, you may destroy target creature with power 1 or less."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        val creature = target("target creature with power 1 or less", Targets.CreatureWithPowerAtMost(1))
        effect = Effects.Destroy(creature)
        description = "When this creature enters, you may destroy target creature with power 1 or less."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "95"
        artist = "Daarken"
        flavorText = "Fast and lethal, with a penchant for the weak and infirm."
        imageUri = "https://cards.scryfall.io/normal/front/4/a/4acb66bd-6abd-46dd-a272-09bea66e2917.jpg?1783941989"
    }
}
