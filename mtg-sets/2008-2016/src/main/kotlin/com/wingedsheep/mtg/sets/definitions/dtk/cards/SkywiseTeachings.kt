package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect

/**
 * Skywise Teachings
 * {3}{U}
 * Enchantment
 *
 * Whenever you cast a noncreature spell, you may pay {1}{U}. If you do, create a 2/2 blue Djinn
 * Monk creature token with flying.
 *
 * "You may pay {1}{U}. If you do, ..." is [MayPayManaEffect] — a gate whose payment is the
 * condition, so the token is only minted when the mana is actually paid. The token's art comes from
 * the set's token sheet, so no `imageUri` is spelled here.
 */
val SkywiseTeachings = card("Skywise Teachings") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "Whenever you cast a noncreature spell, you may pay {1}{U}. If you do, create a 2/2 blue Djinn Monk creature token with flying."

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{1}{U}"),
            effect = Effects.CreateToken(
                power = 2,
                toughness = 2,
                colors = setOf(Color.BLUE),
                creatureTypes = setOf("Djinn", "Monk"),
                keywords = setOf(Keyword.FLYING)
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "79"
        artist = "Filip Burburan"
        flavorText = "Ojutai's words must be translated from Draconic before his students can benefit from their wisdom."
        imageUri = "https://cards.scryfall.io/normal/front/8/7/87875281-12d1-45b9-b1a9-fe0ae7448ab8.jpg?1783938602"
    }
}
