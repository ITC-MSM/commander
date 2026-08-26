package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.PreventDamageEffect

/**
 * Djeru's Resolve
 * {W}
 * Instant
 * Untap target creature. Prevent all damage that would be dealt to it this turn.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 *
 * "it" is the same creature both halves act on, so there is one named target and both effects bind
 * to it. No `Effects.*` facade spells the plain "prevent all damage that would be dealt to target
 * this turn" shield — every parameter is [PreventDamageEffect]'s own default except the recipient.
 */
val DjerusResolve = card("Djeru's Resolve") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Untap target creature. Prevent all damage that would be dealt to it this turn.\n" +
            "Cycling {2} ({2}, Discard this card: Draw a card.)"

    spell {
        val creature = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.Untap(creature),
            PreventDamageEffect(target = creature)
        )
    }

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "11"
        artist = "Kieran Yanner"
        flavorText = "\"When I wish to be strong, I train. When I wish to be wise, I study. When I wish to rest, I start again.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/d/ad60ebb9-4acc-41a8-90d9-04d55e414ed1.jpg?1783936540"
    }
}
