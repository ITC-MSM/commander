package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Eidolon of Inspiration
 * {1}{W}{W}
 * Enchantment Creature — Spirit
 * 2/2
 *
 * At the beginning of combat on your turn, target creature you control gets +2/+0 until end of turn.
 *
 * "on your turn" is the `StepEvent`'s player scope, not a condition: [Triggers.BeginCombat] is
 * `StepEvent(BEGIN_COMBAT, Player.You)`. Its each-combat sibling ([Triggers.EachCombat], which
 * Stampede Rider in this same set uses) differs only in that field.
 */
val EidolonOfInspiration = card("Eidolon of Inspiration") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment Creature — Spirit"
    power = 2
    toughness = 2
    oracleText = "At the beginning of combat on your turn, target creature you control gets +2/+0 until end of turn."

    triggeredAbility {
        trigger = Triggers.BeginCombat
        val creature = target("target", Targets.CreatureYouControl)
        effect = Effects.ModifyStats(2, 0, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "271"
        artist = "Bram Sels"
        flavorText = "\"Agios was felled by a minotaur's blade, but I swear I have seen him since—in the midst of " +
            "battle, wherever help is needed most.\"\n—Phrokos, soldier of Akros"
        imageUri = "https://cards.scryfall.io/normal/front/5/3/53343a0c-d97c-4d6f-b696-5343a962e3dd.jpg"
        inBooster = false
    }
}
