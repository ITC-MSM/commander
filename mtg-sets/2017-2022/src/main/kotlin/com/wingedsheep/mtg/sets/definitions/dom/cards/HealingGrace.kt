package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Healing Grace
 * {W}
 * Instant
 * Prevent the next 3 damage that would be dealt to any target this turn by a source of your choice.
 * You gain 3 life.
 */
val HealingGrace = card("Healing Grace") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Prevent the next 3 damage that would be dealt to any target this turn by a source of your choice. You gain 3 life."

    spell {
        val t = target("any target", Targets.Any)
        effect = Effects.PreventNextDamageFromChosenSource(3, t)
            .then(Effects.GainLife(3))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "20"
        artist = "Magali Villeneuve"
        flavorText = "\"Whatever faith you have in Serra, she has more in you.\" —Lyra Dawnbringer"
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f7512d31-dc35-4046-a1ba-49b74239c329.jpg?1562745837"
    }
}
