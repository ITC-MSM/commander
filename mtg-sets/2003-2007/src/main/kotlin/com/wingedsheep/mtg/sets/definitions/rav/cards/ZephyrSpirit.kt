package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Zephyr Spirit
 * {5}{U}
 * Creature — Spirit
 * 0/6
 *
 * When this creature blocks, return it to its owner's hand.
 *
 * The trigger resolves during the declare blockers step, before combat damage — the
 * attacker it blocked stays blocked (CR 506.4, 509.1h), so removing Zephyr Spirit does not
 * let that creature through.
 */
val ZephyrSpirit = card("Zephyr Spirit") {
    manaCost = "{5}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit"
    oracleText = "When this creature blocks, return it to its owner's hand."
    power = 0
    toughness = 6

    triggeredAbility {
        trigger = Triggers.Blocks
        effect = Effects.ReturnToHand(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "76"
        artist = "Tomas Giorello"
        flavorText = "A spiteful force exists on Ravnica that binds these ghosts to the land of the living but forbids them to touch it."
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e6cbe78f-4325-416b-bf23-282efed5b407.jpg?1783943675"
    }
}
