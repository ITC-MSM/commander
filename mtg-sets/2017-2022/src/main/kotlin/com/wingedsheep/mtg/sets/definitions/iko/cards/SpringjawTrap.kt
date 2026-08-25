package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Springjaw Trap
 * {1}
 * Artifact
 * Flash
 * {4}, {T}, Sacrifice this artifact: It deals 3 damage to any target.
 *
 * Flash lets the trap land during an opponent's turn, but the {T} in the activation cost means it
 * still has to wait out summoning-sickness-free artifact timing — an artifact isn't summoning
 * sick, so it can be deployed and fired in the same window as long as the {4} is available.
 */
val SpringjawTrap = card("Springjaw Trap") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Flash\n{4}, {T}, Sacrifice this artifact: It deals 3 damage to any target."

    keywords(Keyword.FLASH)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}"), Costs.Tap, Costs.SacrificeSelf)
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(3, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "241"
        artist = "Zoltan Boros"
        flavorText = "\"Stop trying to pierce the hide. Hit where it's softest: ankle, sole, hamstring.\"\n—Master Hunter Chevill"
        imageUri = "https://cards.scryfall.io/normal/front/3/7/3741de51-ea92-493a-8058-e0f2000e7701.jpg"
    }
}
