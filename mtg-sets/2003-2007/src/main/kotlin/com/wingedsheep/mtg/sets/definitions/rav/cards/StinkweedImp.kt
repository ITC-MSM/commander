package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val StinkweedImp = card("Stinkweed Imp") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Imp"
    oracleText = "Flying\nWhenever this creature deals combat damage to a creature, destroy that creature.\nDredge 5 (If you would draw a card, you may mill five cards instead. If you do, return this card from your graveyard to your hand.)"
    power = 1
    toughness = 2

    keywordAbility(KeywordAbility.dredge(5))

    keywords(Keyword.FLYING)
    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToCreature
        effect = Effects.Destroy(EffectTarget.TriggeringEntity)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "107"
        artist = "Edward P. Beard, Jr."
        imageUri = "https://cards.scryfall.io/normal/front/6/2/628903a0-6695-4643-80f3-9a6efc4d6a27.jpg?1783943661"
        ruling("2021-03-19", "Notably, Stinkweed Imp's ability isn't deathtouch. It's a triggered ability that triggers only on combat damage.")
    }
}
