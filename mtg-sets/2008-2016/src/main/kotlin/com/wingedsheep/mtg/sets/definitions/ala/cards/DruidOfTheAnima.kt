package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Druid of the Anima
 * {1}{G}
 * Creature — Elf Druid
 * 1 / 1
 * {T}: Add {R}, {G}, or {W}.
 *
 * The Naya answer to the shard obelisks. The printed "or" is a choice between three separate mana
 * abilities, so it is authored as three [Effects.AddMana] abilities sharing [Costs.Tap] — the same
 * shape as [ObeliskOfEsper] — each flagged `manaAbility` with [TimingRule.ManaAbility] so it never
 * uses the stack.
 */
val DruidOfTheAnima = card("Druid of the Anima") {
    manaCost = "{1}{G}"
    colorIdentity = "GRW"
    typeLine = "Creature — Elf Druid"
    power = 1
    toughness = 1
    oracleText = "{T}: Add {R}, {G}, or {W}."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "128"
        artist = "Jim Murray"
        flavorText = "Although the Anima herself remains at the Sacellum, her druids roam Naya, collecting mana bonds with every location in the world."
        imageUri = "https://cards.scryfall.io/normal/front/d/e/de32bf78-73c2-4cd4-b3b3-ef8be53e1e5e.jpg"
    }
}
