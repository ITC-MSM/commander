package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Zuran Spellcaster
 * {2}{U}
 * Creature — Human Wizard
 * 1/1
 *
 * {T}: This creature deals 1 damage to any target.
 *
 * The Prodigal Sorcerer shape: [Costs.Tap] plus [Effects.DealDamage] onto [Targets.Any].
 * "This creature deals" is the ability's own source, which is the facade default — no
 * `damageSource` rider.
 */
val ZuranSpellcaster = card("Zuran Spellcaster") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 1
    toughness = 1
    oracleText = "{T}: This creature deals 1 damage to any target."

    activatedAbility {
        cost = Costs.Tap
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(1, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "112"
        artist = "Edward P. Beard, Jr."
        flavorText = "\"A mage must be precise as well as potent; cautious, as well as clever.\"\n—Zur the Enchanter"
        imageUri = "https://cards.scryfall.io/normal/front/1/5/152a72b1-a7b7-4e5c-8558-fab97465f549.jpg"
    }
}
