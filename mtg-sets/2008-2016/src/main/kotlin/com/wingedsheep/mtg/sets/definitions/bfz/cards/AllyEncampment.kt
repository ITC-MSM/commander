package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Ally Encampment
 * Land
 * {T}: Add {C}.
 * {T}: Add one mana of any color. Spend this mana only to cast an Ally spell.
 * {1}, {T}, Sacrifice this land: Return target Ally you control to its owner's hand.
 */
val AllyEncampment = card("Ally Encampment") {
    manaCost = ""
    colorIdentity = ""
    typeLine = "Land"
    oracleText = "{T}: Add {C}.\n" +
        "{T}: Add one mana of any color. Spend this mana only to cast an Ally spell.\n" +
        "{1}, {T}, Sacrifice this land: Return target Ally you control to its owner's hand."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaOfChoice(
            restriction = ManaRestriction.SubtypeSpellsOnly(setOf("Ally")),
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap, Costs.SacrificeSelf)
        val ally = target(
            "target Ally you control",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.Permanent.withSubtype("Ally").youControl())),
        )
        effect = Effects.ReturnToHand(ally)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "228"
        artist = "Jonas De Ro"
        imageUri = "https://cards.scryfall.io/normal/front/b/c/bcb7124c-ba69-4da8-ad81-58f00fd0181d.jpg?1783938175"
    }
}
