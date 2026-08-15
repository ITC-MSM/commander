package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Firefist Adept
 * {4}{R}
 * Creature — Human Wizard
 * 3/3
 * When Firefist Adept enters the battlefield, it deals X damage to target creature
 * an opponent controls, where X is the number of Wizards you control.
 */
val FirefistAdept = card("Firefist Adept") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Wizard"
    power = 3
    toughness = 3
    oracleText = "When Firefist Adept enters the battlefield, it deals X damage to target creature an opponent controls, where X is the number of Wizards you control."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("creature", Targets.CreatureOpponentControls)
        effect = Effects.DealDamage(
            DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Creature.withSubtype("Wizard")),
            creature
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "121"
        artist = "Lucas Graciano"
        flavorText = "\"The versatile 'fiery gauntlet' is among the first spells young Ghitu mages learn.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/5/0569365d-9e50-436f-a8f3-90820ef06381.jpg?1562730830"
    }
}
