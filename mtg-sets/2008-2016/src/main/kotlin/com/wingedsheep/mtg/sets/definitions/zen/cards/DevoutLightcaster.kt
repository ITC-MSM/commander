package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Devout Lightcaster
 * {W}{W}{W}
 * Creature — Kor Cleric
 * 2/2
 * Protection from black
 * When this creature enters, exile target black permanent.
 *
 * "Target black permanent" is a *permanent* filter, not a creature one — the exile can take a
 * black land or enchantment as readily as a creature.
 */
val DevoutLightcaster = card("Devout Lightcaster") {
    manaCost = "{W}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kor Cleric"
    power = 2
    toughness = 2
    oracleText = "Protection from black\n" +
        "When this creature enters, exile target black permanent."

    keywordAbility(KeywordAbility.protectionFrom(Color.BLACK))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val permanent = target("permanent", TargetPermanent(filter = TargetFilter.Permanent.withColor(Color.BLACK)))
        effect = Effects.Exile(permanent)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "10"
        artist = "Shelly Wan"
        flavorText = "\"Goddess, grant us light to banish the world's shadows.\"\n—Prayer to Kamsa"
        imageUri = "https://cards.scryfall.io/normal/front/1/6/16e31e43-84d3-429e-9e81-60b325989c93.jpg"
    }
}
