package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Aquatic Incursion
 * {3}{U}
 * Enchantment
 * When this enchantment enters, create two 1/1 blue Merfolk creature tokens with hexproof. (They can't be the targets of spells or abilities your opponents control.)
 * {3}{U}: Target Merfolk can't be blocked this turn.
 */
val AquaticIncursion = card("Aquatic Incursion") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, create two 1/1 blue Merfolk creature tokens with hexproof. (They can't be the targets of spells or abilities your opponents control.)\n{3}{U}: Target Merfolk can't be blocked this turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLUE),
            creatureTypes = setOf("Merfolk"),
            keywords = setOf(Keyword.HEXPROOF),
            count = 2,
            imageUri = "https://cards.scryfall.io/normal/front/f/5/f5d353ad-7160-41fa-809c-d76b36478a2a.jpg?1783913608"
        )
    }

    activatedAbility {
        cost = Costs.Mana("{3}{U}")
        val merfolk = target(
            "Merfolk",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.Permanent.withSubtype(Subtype.MERFOLK)))
        )
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, merfolk)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "32"
        artist = "Jason Rainville"
        imageUri = "https://cards.scryfall.io/normal/front/5/6/56cb8fa2-337b-4596-9c31-01f0c0b171b7.jpg?1783935328"
        ruling("2018-01-19", "Activating the last ability of Aquatic Incursion after a Merfolk has become blocked won't cause it to become unblocked.")
    }
}
