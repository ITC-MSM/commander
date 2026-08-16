package com.wingedsheep.mtg.sets.definitions.eve.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Archon of Justice
 * {3}{W}{W}
 * Creature — Archon
 * 4/4
 * Flying
 * When this creature dies, exile target permanent.
 *
 * [Triggers.Dies] plus a mandatory [Effects.Exile] on a [Targets.Permanent] — any permanent,
 * including one you control and including lands.
 */
val ArchonOfJustice = card("Archon of Justice") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Archon"
    power = 4
    toughness = 4
    oracleText = "Flying\nWhen this creature dies, exile target permanent."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Dies
        val t = target("target permanent", Targets.Permanent)
        effect = Effects.Exile(t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "1"
        artist = "Jason Chan"
        flavorText = "In dark times, Truth bears a blade."
        imageUri = "https://cards.scryfall.io/normal/front/a/b/ab707e7f-8ab5-43f1-9428-6a17c1b672fa.jpg?1783942696"
    }
}
