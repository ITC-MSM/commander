package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ashen Rider
 * {4}{W}{W}{B}{B}
 * Creature — Archon
 * 5 / 5
 *
 * Flying
 * When this creature enters or dies, exile target permanent.
 *
 * "Enters **or** dies" is two separate triggered abilities sharing one printed sentence
 * (CR 603.1) — each declares its own target, so the dies half targets afresh rather than
 * reusing the entry half's choice.
 */
val AshenRider = card("Ashen Rider") {
    manaCost = "{4}{W}{W}{B}{B}"
    colorIdentity = "WB"
    typeLine = "Creature — Archon"
    power = 5
    toughness = 5
    oracleText = "Flying\nWhen this creature enters or dies, exile target permanent."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", Targets.Permanent)
        effect = Effects.Exile(t)
    }

    triggeredAbility {
        trigger = Triggers.Dies
        val t = target("target", Targets.Permanent)
        effect = Effects.Exile(t)
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "187"
        artist = "Chris Rahn"
        flavorText = "One offering to appease her on her arrival. Another to celebrate her departure."
        imageUri = "https://cards.scryfall.io/normal/front/6/0/602e43b1-1b1c-4eb1-be0a-61b673646c6f.jpg"
    }
}
