package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Stormplain Detainment
 * {2}{W}
 * Enchantment
 * When this enchantment enters, exile target nonland permanent an opponent controls
 * until this enchantment leaves the battlefield.
 *
 * Modeled with paired ETB / LTB triggers using LinkedExileComponent for the link,
 * the same shape as Banishing Light.
 */
val StormplainDetainment = card("Stormplain Detainment") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, exile target nonland permanent an opponent " +
        "controls until this enchantment leaves the battlefield."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val permanent = target(
            "nonland permanent an opponent controls",
            TargetPermanent(filter = TargetFilter.NonlandPermanentOpponentControls)
        )
        effect = Effects.ExileUntilLeaves(permanent)
    }

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.ReturnLinkedExileUnderOwnersControl()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "28"
        artist = "Livia Prima"
        imageUri = "https://cards.scryfall.io/normal/front/3/9/39f3aab5-7b54-4b55-8114-c6f9f79c255d.jpg?1743204069"
    }
}
