package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
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
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Mudflat Village
 * Land
 *
 * {T}: Add {C}.
 * {T}: Add {B}. Spend this mana only to cast a creature spell.
 * {1}{B}, {T}, Sacrifice this land: Return target Bat, Lizard, Rat, or Squirrel card
 * from your graveyard to your hand.
 */
val MudflatVillage = card("Mudflat Village") {
    typeLine = "Land"
    colorIdentity = "B"
    oracleText = "{T}: Add {C}.\n{T}: Add {B}. Spend this mana only to cast a creature spell.\n" +
        "{1}{B}, {T}, Sacrifice this land: Return target Bat, Lizard, Rat, or Squirrel card " +
        "from your graveyard to your hand."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddColorlessManaEffect(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLACK, restriction = ManaRestriction.CreatureSpellsOnly)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{B}"), Costs.Tap, Costs.SacrificeSelf)
        val t = target(
            "Bat, Lizard, Rat, or Squirrel card in your graveyard",
            TargetObject(
                filter = TargetFilter(
                    GameObjectFilter.Creature
                        .withAnyOfSubtypes(
                            listOf(
                                Subtype("Bat"),
                                Subtype("Lizard"),
                                Subtype("Rat"),
                                Subtype("Squirrel")
                            )
                        )
                        .ownedByYou(),
                    zone = Zone.GRAVEYARD
                )
            )
        )
        effect = Effects.ReturnToHand(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "257"
        artist = "Samuele Bandini"
        imageUri = "https://cards.scryfall.io/normal/front/5/3/53ec4ad3-9cf0-4f1b-a9db-d63feee594ab.jpg?1721639657"
    }
}
