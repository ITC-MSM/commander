package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Channeler Initiate
 * {1}{G}
 * Creature — Human Druid
 * 3/4
 * When this creature enters, put three -1/-1 counters on target creature you control.
 * {T}, Remove a -1/-1 counter from this creature: Add one mana of any color.
 *
 * The three counters are an ordinary *targeted* enters trigger, not an enters-with-counters
 * replacement — the printed line lets them land on any creature you control, and Channeler
 * Initiate itself is only the usual choice. Removing one to pay the mana ability grows the
 * body back, so the counters are the ability's fuel rather than a fixed drawback.
 */
val ChannelerInitiate = card("Channeler Initiate") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Druid"
    oracleText = "When this creature enters, put three -1/-1 counters on target creature you control.\n" +
            "{T}, Remove a -1/-1 counter from this creature: Add one mana of any color."
    power = 3
    toughness = 4

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.AddCounters(Counters.MINUS_ONE_MINUS_ONE, 3, creature)
        description = "When this creature enters, put three -1/-1 counters on target creature you control."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.RemoveCounterFromSelf(Counters.MINUS_ONE_MINUS_ONE))
        effect = Effects.AddManaOfChoice()
        manaAbility = true
        description = "{T}, Remove a -1/-1 counter from this creature: Add one mana of any color."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "160"
        artist = "Yongjae Choi"
        imageUri = "https://cards.scryfall.io/normal/front/b/e/bef38c5a-07fc-477f-a1c0-70cc3ad64f96.jpg?1783936478"
    }
}
