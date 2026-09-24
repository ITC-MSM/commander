package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.Aggregation
import com.wingedsheep.sdk.scripting.values.CardNumericProperty
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.ManaColorSet

/**
 * Loot, the Nexus — the same mana ability as Selvala, Eager Trailblazer: a color choice feeding
 * [Effects.AddManaOfChoice], sized by the number of distinct powers among creatures you control
 * ([Aggregation.DISTINCT_VALUES] over [CardNumericProperty.POWER]), counted as the ability resolves.
 */
val LootTheNexus = card("Loot, the Nexus") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Beast Noble"
    power = 2
    toughness = 1
    oracleText = "{T}: Choose a color. Add one mana of that color for each different power among " +
        "creatures you control."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaOfChoice(
            colorSet = ManaColorSet.AnyColor,
            amount = DynamicAmount.AggregateBattlefield(
                player = Player.You,
                filter = GameObjectFilter.Creature,
                aggregation = Aggregation.DISTINCT_VALUES,
                property = CardNumericProperty.POWER,
            ),
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
        description = "Choose a color. Add one mana of that color for each different power among " +
            "creatures you control."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "262"
        artist = "Filip Burburan"
        flavorText = "\"Within Loot's mind were the blueprints I needed to complete this new Multiverse. " +
            "He gave me hope. Tell him that, Vraska.\"\n—Jace"
        imageUri = "https://cards.scryfall.io/normal/front/3/c/3cfa4fc6-4d90-4576-a83f-6496c7f21104.jpg?1789614764"
        inBooster = false
    }
}
