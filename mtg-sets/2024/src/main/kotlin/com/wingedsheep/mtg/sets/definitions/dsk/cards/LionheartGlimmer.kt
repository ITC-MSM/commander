package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.WardCost
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Lionheart Glimmer
 * {3}{W}{W}
 * Enchantment Creature — Cat Glimmer
 * 2/5
 *
 * Ward {2}
 * Whenever you attack, creatures you control get +1/+1 until end of turn.
 */
val LionheartGlimmer = card("Lionheart Glimmer") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment Creature — Cat Glimmer"
    power = 2
    toughness = 5
    oracleText = "Ward {2} (Whenever this creature becomes the target of a spell or ability an opponent " +
        "controls, counter it unless that player pays {2}.)\n" +
        "Whenever you attack, creatures you control get +1/+1 until end of turn."

    keywordAbility(KeywordAbility.Ward(WardCost.Mana("{2}")))

    // Whenever you attack, creatures you control get +1/+1 until end of turn.
    triggeredAbility {
        trigger = Triggers.YouAttack
        effect = Effects.ForEachInGroup(
            filter = GroupFilter.AllCreaturesYouControl,
            effect = Effects.ModifyStats(1, 1, EffectTarget.Self),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "19"
        artist = "Josu Hernaiz"
        flavorText = "The survivors would no longer be picked off one by one. It was their turn to hunt."
        imageUri = "https://cards.scryfall.io/normal/front/4/8/483e1c6f-331c-45f1-bf5d-9b9742aa8903.jpg?1726285926"
    }
}
