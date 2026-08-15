package com.wingedsheep.mtg.sets.definitions.eld.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ChoiceType
import com.wingedsheep.sdk.scripting.EntersWithChoice
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Heraldic Banner
 * {3}
 * Artifact
 * As this artifact enters, choose a color.
 * Creatures you control of the chosen color get +1/+0.
 * {T}: Add one mana of the chosen color.
 *
 * The chosen color is stored via [EntersWithChoice]`(ChoiceType.COLOR)`; the lord filter
 * (`withChosenColor()`) and the mana ability ([Effects.AddManaOfChosenColor]) both read it
 * from the source.
 */
val HeraldicBanner = card("Heraldic Banner") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "As this artifact enters, choose a color.\n" +
        "Creatures you control of the chosen color get +1/+0.\n" +
        "{T}: Add one mana of the chosen color."

    replacementEffect(EntersWithChoice(ChoiceType.COLOR))

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 0,
            filter = GroupFilter(GameObjectFilter.Creature.youControl().withChosenColor())
        )
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaOfChosenColor()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "222"
        artist = "Ravenna Tran"
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2e349af5-3f25-46d3-908e-83b2f6028b95.jpg?1782707787"
    }
}
