package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Guardian Gladewalker
 * {1}{G}
 * Creature — Shapeshifter
 * 1/1
 * Changeling (This card is every creature type.)
 * When this creature enters, put a +1/+1 counter on target creature.
 *
 * Changeling is a characteristic-defining ability the engine's StateProjector reads straight off
 * `Keyword.CHANGELING`, so the Gladewalker is every creature type in every zone — which is what makes
 * a 1/1 for two an Elf, a Dwarf and a Shapeshifter at once.
 */
val GuardianGladewalker = card("Guardian Gladewalker") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Shapeshifter"
    oracleText = "Changeling (This card is every creature type.)\n" +
        "When this creature enters, put a +1/+1 counter on target creature."
    power = 1
    toughness = 1

    keywords(Keyword.CHANGELING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val recipient = target("target creature", Targets.Creature)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, recipient)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "174"
        artist = "Mila Pesic"
        flavorText = "The raiders' only warning was a whisper floating through the mist: \"Trespassers.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/8/587eed42-5111-4161-9af5-bf76556c542a.jpg"
    }
}
