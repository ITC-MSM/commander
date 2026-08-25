package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Fathom Fleet Firebrand
 * {1}{R}
 * Creature — Human Pirate
 * 2/2
 *
 * {1}{R}: This creature gets +1/+0 until end of turn.
 */
val FathomFleetFirebrand = card("Fathom Fleet Firebrand") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Pirate"
    oracleText = "{1}{R}: This creature gets +1/+0 until end of turn."
    power = 2
    toughness = 2

    activatedAbility {
        cost = Costs.Mana("{1}{R}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "142"
        artist = "Zezhou Chen"
        flavorText = "As she charges into battle, her arcane tattoos stir and crawl like fiery serpents."
        imageUri = "https://cards.scryfall.io/normal/front/5/2/52280963-ba5b-4735-b5cb-67866f8624c9.jpg"
    }
}
