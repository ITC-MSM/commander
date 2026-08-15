package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Mighty Mutanimals
 * {2}{W}{W}
 * Creature — Mutant Rebel
 * 2/1
 *
 * When this creature enters, create a 2/2 red Mutant creature token.
 * Alliance — Whenever another creature you control enters, put a
 * +1/+1 counter on target creature you control.
 */
val MightyMutanimals = card("Mighty Mutanimals") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Mutant Rebel"
    oracleText = "When this creature enters, create a 2/2 red Mutant creature token.\nAlliance — Whenever another creature you control enters, put a +1/+1 counter on target creature you control."
    power = 2
    toughness = 1

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = CreateTokenEffect(
            power = 2,
            toughness = 2,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Mutant"),
            imageUri = "https://cards.scryfall.io/normal/front/5/1/51e33613-7a24-461c-8d9f-12680af4b92a.jpg?1771590526"
        )
    }

    triggeredAbility {
        trigger = Triggers.OtherCreatureEnters
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
        description = "Alliance — Whenever another creature you control enters, put a +1/+1 counter on target creature you control."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "21"
        artist = "Manuel Castañón"
        flavorText = "\"Yer right. We're just a scrappy band of mutants. We can't win. But boy, can we stir up trouble.\"\n—Old Hob"
        imageUri = "https://cards.scryfall.io/normal/front/5/d/5dd5369c-174c-450b-b776-553866787f8f.jpg?1771502533"
    }
}
