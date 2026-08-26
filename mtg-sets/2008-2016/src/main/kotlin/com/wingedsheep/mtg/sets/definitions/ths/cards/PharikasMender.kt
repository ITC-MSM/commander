package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Pharika's Mender
 * {3}{B}{G}
 * Creature — Gorgon
 * 4 / 3
 *
 * When this creature enters, you may return target creature or enchantment card from your graveyard to your hand.
 *
 * - The graveyard is carried by the *target filter's* `zone`, not by a `fromZone` guard on the move:
 *   a targeted return re-checks its target's legality at resolution (CR 608.2b), so the guard would
 *   be redundant. [Effects.ReturnToHand]'s KDoc spells out the same split.
 * - The printed "you may" is the builder's `optional = true`, which lowers to a `Gate.MayDecide`
 *   around the return.
 */
val PharikasMender = card("Pharika's Mender") {
    manaCost = "{3}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Gorgon"
    power = 4
    toughness = 3
    oracleText = "When this creature enters, you may return target creature or enchantment card from your graveyard to your hand."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val recovered = target(
            "creature or enchantment card in your graveyard",
            TargetObject(
                filter = TargetFilter(
                    GameObjectFilter.CreatureOrEnchantment.ownedByYou(),
                    zone = Zone.GRAVEYARD
                )
            )
        )
        optional = true
        effect = Effects.ReturnToHand(recovered)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "197"
        artist = "Peter Mohrbacher"
        flavorText = "\"The direst venom becomes a panacea under Pharika's guidance. I bring it to the worthy, clinging at the edge of the abyss.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f3dcd1ce-717c-431a-9895-f7701d276743.jpg"
    }
}
