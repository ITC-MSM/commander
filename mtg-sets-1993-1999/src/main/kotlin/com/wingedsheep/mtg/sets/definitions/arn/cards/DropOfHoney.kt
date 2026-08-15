package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Drop of Honey
 * {G}
 * Enchantment
 * At the beginning of your upkeep, destroy the creature with the least power. It can't be
 * regenerated. If two or more creatures are tied for least power, you choose one of them.
 * When there are no creatures on the battlefield, sacrifice this enchantment.
 *
 * Composition (one new SDK primitive — the global least-power selector):
 *  - The upkeep trigger uses [Effects.DestroyLeastPowerCreature], which gathers every creature
 *    tied for the global minimum power (`HasLeastPowerAmongAllCreatures`), lets the controller
 *    pick one (auto when unique, a choice on a tie), and destroys it without regeneration.
 *  - "When there are no creatures on the battlefield" is a `stateTriggeredAbility`
 *    (CR 603.8) gated on the global [Conditions.NoCreaturesOnBattlefield], mirroring Serendib
 *    Djinn's "when you control no lands" self-sacrifice.
 */
val DropOfHoney = card("Drop of Honey") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "At the beginning of your upkeep, destroy the creature with the least power. " +
        "It can't be regenerated. If two or more creatures are tied for least power, you choose one of them.\n" +
        "When there are no creatures on the battlefield, sacrifice this enchantment."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.DestroyLeastPowerCreature(noRegenerate = true)
        description = "At the beginning of your upkeep, destroy the creature with the least power. " +
            "It can't be regenerated. If two or more creatures are tied for least power, you choose one of them."
    }

    stateTriggeredAbility {
        condition = Conditions.NoCreaturesOnBattlefield
        effect = Effects.SacrificeTarget(EffectTarget.Self)
        description = "When there are no creatures on the battlefield, sacrifice this enchantment"
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "47"
        artist = "Anson Maddocks"
        imageUri = "https://cards.scryfall.io/normal/front/2/6/26e090d4-e7fe-403c-9aca-05c1b45ed238.jpg?1562902230"
    }
}
