package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Sun-Blessed Healer
 * {1}{W}
 * Creature — Human Cleric
 * 3/1
 * Kicker {1}{W}
 * Lifelink
 * When this creature enters, if it was kicked, return target nonland permanent card with
 * mana value 2 or less from your graveyard to the battlefield.
 */
val SunBlessedHealer = card("Sun-Blessed Healer") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 3
    toughness = 1
    oracleText = "Kicker {1}{W} (You may pay an additional {1}{W} as you cast this spell.)\n" +
        "Lifelink\n" +
        "When this creature enters, if it was kicked, return target nonland permanent card with mana value 2 or less from your graveyard to the battlefield."

    keywordAbility(KeywordAbility.kicker("{1}{W}"))
    keywords(Keyword.LIFELINK)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = WasKicked
        val t = target(
            "target",
            TargetObject(
                filter = TargetFilter(
                    GameObjectFilter.NonlandPermanent.ownedByYou(),
                    zone = Zone.GRAVEYARD
                ).manaValueAtMost(2)
            )
        )
        effect = Effects.Move(t, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "25"
        artist = "Mark Zug"
        imageUri = "https://cards.scryfall.io/normal/front/3/2/323d029e-9a88-4188-b3a4-38ef32cffc9f.jpg?1782689245"
    }
}
