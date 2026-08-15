package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Air Nomad Legacy
 * {W}{U}
 * Enchantment
 *
 * When this enchantment enters, create a Clue token. (It's an artifact with
 * "{2}, Sacrifice this token: Draw a card.")
 * Creatures you control with flying get +1/+1.
 */
val AirNomadLegacy = card("Air Nomad Legacy") {
    manaCost = "{W}{U}"
    colorIdentity = "WU"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, create a Clue token. " +
        "(It's an artifact with \"{2}, Sacrifice this token: Draw a card.\")\n" +
        "Creatures you control with flying get +1/+1."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateClue()
    }

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(GameObjectFilter.Creature.youControl().withKeyword(Keyword.FLYING))
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "206"
        artist = "AKAGI"
        flavorText = "Ancient stories of flying monks inspired generations of gliders to come."
        imageUri = "https://cards.scryfall.io/normal/front/1/0/10874416-fa4c-4aa3-b885-4351d011d208.jpg?1764121440"
    }
}
