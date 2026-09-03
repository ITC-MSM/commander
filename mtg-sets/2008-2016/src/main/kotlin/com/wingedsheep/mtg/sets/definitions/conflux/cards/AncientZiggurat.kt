package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ManaRestriction

/**
 * Ancient Ziggurat
 * Land
 *
 * {T}: Add one mana of any color. Spend this mana only to cast a creature spell.
 *
 * [Effects.AddManaOfChoice] already defaults to one mana from all five colours, so only the
 * [ManaRestriction.CreatureSpellsOnly] rider needs naming — the restriction rides on the mana in
 * the pool rather than on the ability. `manaAbility = true` plus [TimingRule.ManaAbility] keep it
 * off the stack (CR 605.1a).
 */
val AncientZiggurat = card("Ancient Ziggurat") {
    typeLine = "Land"
    colorIdentity = ""
    oracleText = "{T}: Add one mana of any color. Spend this mana only to cast a creature spell."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaOfChoice(restriction = ManaRestriction.CreatureSpellsOnly)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "141"
        artist = "John Avon"
        flavorText = "Built in honor of Alara's creatures, the ziggurat vanished long ago. When Progenitus awakened, the temple emerged again."
        imageUri = "https://cards.scryfall.io/normal/front/0/3/0348247d-0a70-4961-8590-9de41386c69b.jpg"
    }
}
