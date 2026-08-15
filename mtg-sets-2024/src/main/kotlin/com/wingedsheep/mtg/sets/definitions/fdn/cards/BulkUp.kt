package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Bulk Up
 * {1}{R}
 * Instant
 *
 * Double target creature's power until end of turn.
 * Flashback {4}{R}{R} (You may cast this card from your graveyard for its flashback cost. Then exile it.)
 *
 * "Double its power" is the standard layer-7c +N/+0 modification where N is the creature's power,
 * read at resolution via [DynamicAmount.EntityProperty] on the target — the same shape used by
 * "double power and toughness" cards. The bonus is locked when the effect is applied, so it does
 * not feed back on itself.
 */
val BulkUp = card("Bulk Up") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Double target creature's power until end of turn.\n" +
        "Flashback {4}{R}{R} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"

    spell {
        val creature = target("creature", Targets.Creature)
        effect = Effects.ModifyStats(
            power = DynamicAmount.EntityProperty(EntityReference.Target(0), EntityNumericProperty.Power),
            toughness = DynamicAmount.Fixed(0),
            target = creature
        )
    }

    keywordAbility(KeywordAbility.flashback("{4}{R}{R}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "80"
        artist = "Warren Mahy"
        imageUri = "https://cards.scryfall.io/normal/front/9/7/977dcc50-da10-4281-b522-9240c1204f5d.jpg?1782689196"
    }
}
