package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Baleful Ammit
 * {2}{B}
 * Creature — Crocodile Demon
 * 4/3
 * Lifelink
 * When this creature enters, put a -1/-1 counter on target creature you control.
 */
val BalefulAmmit = card("Baleful Ammit") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Crocodile Demon"
    oracleText = "Lifelink\n" +
            "When this creature enters, put a -1/-1 counter on target creature you control."
    power = 4
    toughness = 3

    keywords(Keyword.LIFELINK)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", Targets.CreatureYouControl)
        effect = Effects.AddCounters(Counters.MINUS_ONE_MINUS_ONE, 1, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "79"
        artist = "Seb McKinnon"
        flavorText = "\"Not all in our crop deserve the afterlife. We must leave the unworthy behind, Samut.\"\n—Djeru, initiate of Tah crop"
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fb662d58-ae50-423d-b1c5-abd45baca12b.jpg?1783936511"
    }
}
