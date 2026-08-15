package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Web Up
 * {2}{W}
 * Enchantment
 * When this enchantment enters, exile target nonland permanent an opponent controls until this enchantment leaves the battlefield.
 */
val WebUp = card("Web Up") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, exile target nonland permanent an opponent controls until this enchantment leaves the battlefield."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", TargetPermanent(filter = TargetFilter.NonlandPermanentOpponentControls))
        effect = Effects.ExileUntilLeaves(t)
    }

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.ReturnLinkedExileUnderOwnersControl()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "21"
        artist = "David Palumbo"
        flavorText = "\"Courtesy of your friendly neighborhood Spider-Man.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/a/1ab7c1e6-54af-4002-8a81-23a1ccafa3ff.jpg?1757376870"
    }
}
