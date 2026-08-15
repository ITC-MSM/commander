package com.wingedsheep.mtg.sets.definitions.gtc.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.Aggregation
import com.wingedsheep.sdk.scripting.values.CardNumericProperty
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Prime Speaker Zegana — GTC #188
 * Legendary Creature — Merfolk Wizard (1/1), Mythic
 *
 * Prime Speaker Zegana enters with X +1/+1 counters on it, where X is the greatest power among
 *   other creatures you control.
 * When Prime Speaker Zegana enters, draw cards equal to its power.
 *
 * X is an entry-replacement (`EntersWithDynamicCounters`), so it is computed once as Zegana
 * enters and `excludeSelf = true` keeps her own 1 power out of the max — a creature entering
 * simultaneously isn't counted either, since it isn't on the battlefield when the replacement
 * evaluates. The draw trigger reads the source's *projected* power, which already includes the
 * counters she entered with, and falls back to last-known information if she has left the
 * battlefield before the trigger resolves.
 */
val PrimeSpeakerZegana = card("Prime Speaker Zegana") {
    manaCost = "{2}{G}{G}{U}{U}"
    colorIdentity = "GU"
    typeLine = "Legendary Creature — Merfolk Wizard"
    power = 1
    toughness = 1
    oracleText = "Prime Speaker Zegana enters with X +1/+1 counters on it, where X is the greatest " +
        "power among other creatures you control.\n" +
        "When Prime Speaker Zegana enters, draw cards equal to its power."

    replacementEffect(
        EntersWithDynamicCounters(
            count = DynamicAmount.AggregateBattlefield(
                player = Player.You,
                filter = GameObjectFilter.Creature,
                aggregation = Aggregation.MAX,
                property = CardNumericProperty.POWER,
                excludeSelf = true,
            ),
        ),
    )

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(DynamicAmounts.sourcePower())
        description = "When Prime Speaker Zegana enters, draw cards equal to its power."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "188"
        artist = "Willian Murai"
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f30dfb8e-f540-45ab-a4e8-63425099646a.jpg?1783940101"

        ruling(
            "2024-11-08",
            "The value of X is calculated only once, as Prime Speaker Zegana's first ability resolves. " +
                "If you control no other creatures at that time, X is 0.",
        )
        ruling(
            "2024-11-08",
            "If Prime Speaker Zegana enters at the same time as another creature you control, you won't " +
                "consider that creature when determining the greatest power among creatures you control.",
        )
        ruling(
            "2024-11-08",
            "If Prime Speaker Zegana is no longer on the battlefield when its last ability resolves, use " +
                "its power as it last existed on the battlefield to determine how many cards to draw.",
        )
    }
}
