package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Constricting Tendrils
 * {U}
 * Instant
 * Target creature gets -3/-0 until end of turn.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 *
 * One [Effects.ModifyStats] over a named target; the facade's default duration is already
 * `Duration.EndOfTurn`, so the printed "until end of turn" needs no argument. The -0 toughness
 * half is still written explicitly — it is the printed modifier, not an omission. Cycling is
 * [KeywordAbility.cycling], which carries its own cost and lowers to the discard-and-draw
 * activated ability.
 */
val ConstrictingTendrils = card("Constricting Tendrils") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Target creature gets -3/-0 until end of turn.\n" +
        "Cycling {2} ({2}, Discard this card: Draw a card.)"

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.ModifyStats(-3, 0, t)
    }

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "22"
        artist = "David Palumbo"
        flavorText = "Priests of Bant protect their temples with traps more elaborate than any mosaic floor."
        imageUri = "https://cards.scryfall.io/normal/front/1/a/1a110be3-93ec-40ef-94a6-e4c43f1ce211.jpg"
    }
}
