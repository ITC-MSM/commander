package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Startled Relic Sloth
 * {2}{R}{W}
 * Creature — Sloth Beast
 * 4/4
 *
 * Trample, lifelink
 * At the beginning of combat on your turn, exile up to one target card from a graveyard.
 */
val StartledRelicSloth = card("Startled Relic Sloth") {
    manaCost = "{2}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Creature — Sloth Beast"
    oracleText = "Trample, lifelink\n" +
        "At the beginning of combat on your turn, exile up to one target card from a graveyard."
    power = 4
    toughness = 4

    keywords(Keyword.TRAMPLE, Keyword.LIFELINK)

    // At the beginning of combat on your turn, exile up to one target card from a graveyard.
    triggeredAbility {
        trigger = Triggers.BeginCombat
        val t = target("target card from a graveyard", TargetObject(optional = true, filter = TargetFilter.CardInGraveyard))
        effect = Effects.Move(t, Zone.EXILE)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "233"
        artist = "David Astruga"
        flavorText = "\"Should your sloth become agitated, remove all valuables from the immediate area " +
            "and wait for it to tire out. This will not take long.\"\n—Lorehold student handbook"
        imageUri = "https://cards.scryfall.io/normal/front/f/1/f143fd41-58c3-45a0-bef8-e9e4b4a502a5.jpg?1775938628"
    }
}
