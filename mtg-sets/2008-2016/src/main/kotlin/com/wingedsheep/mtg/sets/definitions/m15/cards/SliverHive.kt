package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.ManaColorSet

/**
 * Sliver Hive
 * Land
 * {T}: Add {C}.
 * {T}: Add one mana of any color. Spend this mana only to cast a Sliver spell.
 * {5}, {T}: Create a 1/1 colorless Sliver creature token. Activate only if you control a Sliver.
 *
 * "You control a Sliver" is any Sliver *permanent*, not just a creature — the Hive's own token is
 * one, so the first activation bootstraps the rest.
 */
val SliverHive = card("Sliver Hive") {
    typeLine = "Land"
    oracleText =
        "{T}: Add {C}.\n" +
        "{T}: Add one mana of any color. Spend this mana only to cast a Sliver spell.\n" +
        "{5}, {T}: Create a 1/1 colorless Sliver creature token. Activate only if you control a Sliver."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaOfChoice(
            colorSet = ManaColorSet.AnyColor,
            restriction = ManaRestriction.SubtypeSpellsOnly(setOf("Sliver"))
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{5}"), Costs.Tap)
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            creatureTypes = setOf("Sliver"),
        )
        restrictions = listOf(
            ActivationRestriction.OnlyIfCondition(
                Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Permanent.withSubtype("Sliver"))
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "247"
        artist = "Igor Kieryluk"
        imageUri = "https://cards.scryfall.io/normal/front/9/1/91cef7ce-aa9f-4659-ac24-394c5ab9f77c.jpg?1783939151"
    }
}
