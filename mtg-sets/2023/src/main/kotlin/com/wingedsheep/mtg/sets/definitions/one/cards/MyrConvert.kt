package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Myr Convert
 * {2}
 * Artifact Creature — Phyrexian Myr
 * 2/1
 *
 * Toxic 1 (Players dealt combat damage by this creature also get a poison counter.)
 * {T}, Pay 2 life: Add one mana of any color.
 *
 * The mana ability is Phyrexian Lens' shape with a bigger life payment: a composite
 * [Costs.Tap] + [Costs.PayLife] cost feeding [Effects.AddAnyColorMana], flagged as a mana
 * ability so it never uses the stack (CR 605.1a).
 */
val MyrConvert = card("Myr Convert") {
    manaCost = "{2}"
    typeLine = "Artifact Creature — Phyrexian Myr"
    power = 2
    toughness = 1
    oracleText = "Toxic 1 (Players dealt combat damage by this creature also get a poison counter.)\n" +
        "{T}, Pay 2 life: Add one mana of any color."

    keywordAbility(KeywordAbility.Numeric(Keyword.TOXIC, 1))

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.PayLife(2))
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "234"
        artist = "José Parodi"
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9df0adcf-7ad0-4d70-8dcd-28f69471495b.jpg?1783917989"
    }
}
