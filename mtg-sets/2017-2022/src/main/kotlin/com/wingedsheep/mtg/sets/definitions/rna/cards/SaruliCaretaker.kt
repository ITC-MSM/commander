package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Saruli Caretaker — Ravnica Allegiance #139
 * {G} · Creature — Dryad · 0 / 3
 *
 * "Tap an untapped creature you control" is a [Costs.TapPermanents] atom beside the source's
 * own {T} — two separate tap costs in one composite. The ability produces mana and targets
 * nothing, so it is a mana ability ([TimingRule.ManaAbility], `manaAbility = true`) and never
 * uses the stack.
 */
val SaruliCaretaker = card("Saruli Caretaker") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dryad"
    power = 0
    toughness = 3
    oracleText = "Defender\n" +
        "{T}, Tap an untapped creature you control: Add one mana of any color."

    keywords(Keyword.DEFENDER)
    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.TapPermanents(count = 1, filter = GameObjectFilter.Creature))
        effect = Effects.AddManaOfChoice()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "139"
        artist = "Howard Lyon"
        flavorText = "\"I hold the seed of our new beginning.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/f/ef3358cb-714c-49bf-b7e9-a69d02d7799e.jpg"
    }
}
