package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddColorlessManaEffect
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Rockface Village
 * Land
 * {T}: Add {C}.
 * {T}: Add {R}. Spend this mana only to cast a creature spell.
 * {R}, {T}: Target Lizard, Mouse, Otter, or Raccoon you control gets +1/+0 and gains
 * haste until end of turn. Activate only as a sorcery.
 */
val RockfaceVillage = card("Rockface Village") {
    typeLine = "Land"
    colorIdentity = "R"
    oracleText = "{T}: Add {C}.\n{T}: Add {R}. Spend this mana only to cast a creature spell.\n{R}, {T}: Target Lizard, Mouse, Otter, or Raccoon you control gets +1/+0 and gains haste until end of turn. Activate only as a sorcery."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddColorlessManaEffect(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.RED, restriction = ManaRestriction.CreatureSpellsOnly)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{R}"), Costs.Tap)
        val t = target("target", TargetCreature(
            filter = TargetFilter(
                GameObjectFilter.Creature
                    .youControl()
                    .withAnyOfSubtypes(
                        listOf(
                            Subtype("Lizard"),
                            Subtype("Mouse"),
                            Subtype("Otter"),
                            Subtype("Raccoon")
                        )
                    )
            )
        ))
        effect = Effects.ModifyStats(1, 0, t)
            .then(Effects.GrantKeyword(Keyword.HASTE, t))
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "259"
        artist = "Thomas Stoop"
        imageUri = "https://cards.scryfall.io/normal/front/6/2/62799d24-39a6-4e66-8ac3-7cafa99e6e6d.jpg?1721639562"
    }
}
