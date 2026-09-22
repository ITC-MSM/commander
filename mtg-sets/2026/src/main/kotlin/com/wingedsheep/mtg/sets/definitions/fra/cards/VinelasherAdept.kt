package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

val VinelasherAdept = card("Vinelasher Adept") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Rhino Soldier"
    power = 2
    toughness = 4
    oracleText = "Reach\nWhen this creature enters, put three +1/+1 counters on target creature.\nBasic landcycling {2} ({2}, Discard this card: Search your library for a basic land card, reveal it, put it into your hand, then shuffle.)"

    keywords(Keyword.REACH)
    keywordAbility(KeywordAbility.basicLandcycling("{2}"))
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("creature", Targets.Creature)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 3, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "119"
        artist = "Christina Kraus"
        flavorText = "Some battlevine cultivars can grasp flesh as easily as they can climb walls."
        imageUri = "https://cards.scryfall.io/normal/front/e/5/e5ed142b-2b61-4ef5-8b23-2db2a0a0319d.jpg?1789556814"
        inBooster = false
    }
}
