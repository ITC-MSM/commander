package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.AddCountersToCollectionEffect
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Aerith Gainsborough
 * {2}{W}
 * Legendary Creature — Human Cleric
 * 2/2
 *
 * Lifelink
 * Whenever you gain life, put a +1/+1 counter on Aerith Gainsborough.
 * When Aerith Gainsborough dies, put X +1/+1 counters on each legendary creature you control,
 * where X is the number of +1/+1 counters on Aerith Gainsborough.
 *
 * The dies ability uses last-known information for X: the +1/+1 counter count Aerith had as it
 * last existed on the battlefield ([ContextPropertyKey.LAST_KNOWN_PLUS_ONE_COUNTER_COUNT], the
 * same primitive Hooded Hydra's dies trigger uses). X is locked in when the ability resolves, so
 * a simultaneous lethal -1/-1 counter influx still uses the pre-removal +1/+1 count (Scryfall
 * ruling 2025-06-06). The recipients are gathered fresh at resolution (legendary creatures you
 * control), so Aerith — already in the graveyard — does not buff itself.
 */
val AerithGainsborough = card("Aerith Gainsborough") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Cleric"
    power = 2
    toughness = 2
    oracleText = "Lifelink\nWhenever you gain life, put a +1/+1 counter on Aerith Gainsborough.\n" +
        "When Aerith Gainsborough dies, put X +1/+1 counters on each legendary creature you control, " +
        "where X is the number of +1/+1 counters on Aerith Gainsborough."

    keywords(Keyword.LIFELINK)

    // Whenever you gain life, put a +1/+1 counter on Aerith.
    triggeredAbility {
        trigger = Triggers.YouGainLife
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    // When Aerith dies, put X +1/+1 counters on each legendary creature you control,
    // where X is the number of +1/+1 counters Aerith had as it last existed on the battlefield.
    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.BattlefieldMatching(
                    filter = GameObjectFilter.Creature.youControl().legendary()
                ),
                storeAs = "legendaryCreatures"
            ),
            AddCountersToCollectionEffect(
                collectionName = "legendaryCreatures",
                counterType = Counters.PLUS_ONE_PLUS_ONE,
                amount = DynamicAmount.ContextProperty(ContextPropertyKey.LAST_KNOWN_PLUS_ONE_COUNTER_COUNT)
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "4"
        artist = "Nakamura8"
        imageUri = "https://cards.scryfall.io/normal/front/e/8/e86328b6-ded2-41df-8b6e-4a770e7b171e.jpg?1748705770"

        ruling("2025-06-06", "If Aerith Gainsborough is dealt lethal damage at the same time that you gain life, it will die before its second ability would resolve. Its last ability will use the number of counters that were on it when it was last on the battlefield.")
        ruling("2025-06-06", "In the rare case where enough -1/-1 counters are put on Aerith Gainsborough at the same time to make its toughness 0 or less, the number of +1/+1 counters on it before it got those -1/-1 counters will be used to determine the value of X in its third ability.")
        ruling("2025-06-06", "Aerith Gainsborough's second ability triggers just once for each life-gaining event, no matter how much life was gained.")
        ruling("2025-06-06", "Each creature with lifelink dealing combat damage causes a separate life-gaining event, so Aerith's second ability triggers once per such event.")
    }
}
