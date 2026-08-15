package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Guac & Marshmallow Pizza
 * {G}
 * Artifact — Food
 *
 * Flash
 * When this artifact enters, target creature gets +2/+2 until end of
 * turn. Untap it.
 * {2}, {T}, Sacrifice this artifact: You gain 3 life.
 */
val GuacAndMarshmallowPizza = card("Guac & Marshmallow Pizza") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Artifact — Food"
    oracleText = "Flash\nWhen this artifact enters, target creature gets +2/+2 until end of turn. Untap it.\n{2}, {T}, Sacrifice this artifact: You gain 3 life."

    keywords(Keyword.FLASH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(2, 2, creature)
            .then(Effects.Untap(creature))
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}"),
            Costs.Tap,
            Costs.SacrificeSelf
        )
        effect = Effects.GainLife(3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "116"
        artist = "Brandon L. Hunt"
        flavorText = "\"There is no fear in my heart. I will overcome even this challenge!\"\n—Leonardo"
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a5405de0-16c9-4e5a-8ceb-00508e212f12.jpg?1771502701"
    }
}
