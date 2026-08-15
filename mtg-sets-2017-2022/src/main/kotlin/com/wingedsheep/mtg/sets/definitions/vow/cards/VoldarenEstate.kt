package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Voldaren Estate — Innistrad: Crimson Vow #267
 * Land · Rare
 * Artist: Richard Wright
 *
 * {T}: Add {C}.
 * {T}, Pay 1 life: Add one mana of any color. Spend this mana only to cast a Vampire spell.
 * {5}, {T}: Create a Blood token. This ability costs {1} less to activate for each Vampire you
 * control.
 *
 * The restricted-use colored mana rides on [ManaRestriction.SubtypeSpellsOnly] ("Vampire") — the
 * Bucolic Ranch shape, just with an added [Costs.PayLife] on the cost. The Blood ability's "costs
 * {1} less to activate for each Vampire you control" is [genericCostReduction] over a dynamic count
 * of your Vampires (the Mirror of Galadriel pattern); the reduction only touches the generic {5}, so
 * it can never reduce below {0} and the {T} always remains.
 */
val VoldarenEstate = card("Voldaren Estate") {
    typeLine = "Land"
    oracleText = "{T}: Add {C}.\n" +
        "{T}, Pay 1 life: Add one mana of any color. Spend this mana only to cast a Vampire spell.\n" +
        "{5}, {T}: Create a Blood token. This ability costs {1} less to activate for each Vampire " +
        "you control."

    // {T}: Add {C}.
    activatedAbility {
        cost = AbilityCost.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    // {T}, Pay 1 life: Add one mana of any color. Spend this mana only to cast a Vampire spell.
    activatedAbility {
        cost = Costs.Composite(AbilityCost.Tap, Costs.PayLife(1))
        effect = Effects.AddAnyColorMana(
            amount = 1,
            restriction = ManaRestriction.SubtypeSpellsOnly(setOf("Vampire"))
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    // {5}, {T}: Create a Blood token. Costs {1} less to activate for each Vampire you control.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{5}"), AbilityCost.Tap)
        effect = Effects.CreateBlood()
        genericCostReduction = DynamicAmount.AggregateBattlefield(
            player = Player.You,
            filter = GameObjectFilter.Creature.withSubtype(Subtype.VAMPIRE)
        )
        description = "{5}, {T}: Create a Blood token. This ability costs {1} less to activate for " +
            "each Vampire you control."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "267"
        artist = "Richard Wright"
        imageUri = "https://cards.scryfall.io/normal/front/2/5/2577e5fd-903a-44ce-989a-d53d56511ad3.jpg?1783924780"
    }
}
