package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Llanowar Tribe
 * {G}{G}{G}
 * Creature — Elf Druid
 * 3/3
 * {T}: Add {G}{G}{G}.
 *
 * A Llanowar Elves that adds three at once: one [Effects.AddMana] of [Color.GREEN] for 3, not three
 * separate additions. `manaAbility = true` plus [TimingRule.ManaAbility] is what keeps it off the
 * stack (CR 605.1a) — the same pair Basalt Monolith carries.
 */
val LlanowarTribe = card("Llanowar Tribe") {
    manaCost = "{G}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Druid"
    power = 3
    toughness = 3
    oracleText = "{T}: Add {G}{G}{G}."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN, 3)
        manaAbility = true
        timing = TimingRule.ManaAbility
        description = "{T}: Add {G}{G}{G}."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "170"
        artist = "Scott Murphy"
        flavorText = "\"Llanowar remembers the Ice Age, the Phyrexian Invasion, and the Rift Era. So long as we draw breath, we will ensure such disasters never threaten our world again.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/2/723d3d8d-e78b-40b8-aed5-888a2d0baa60.jpg?1783933094"
    }
}
