package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Entreat the Angels
 * {X}{X}{W}{W}{W}
 * Sorcery
 *
 * Create X 4/4 white Angel creature tokens with flying.
 * Miracle {X}{W}{W} (You may cast this card for its miracle cost when you draw it if it's the first card you drew this turn.)
 *
 * The dynamic-count [Effects.CreateToken] overload with `count = DynamicAmount.XValue`: X is read
 * once at resolution (CR 613.4c) from whatever was announced — the `{X}{X}` of the printed cost, or
 * the single `{X}` of the miracle cost when cast that way. Token art resolves from the set's own
 * `tokenArt`, so no `imageUri` is authored here.
 */
val EntreatTheAngels = card("Entreat the Angels") {
    manaCost = "{X}{X}{W}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Create X 4/4 white Angel creature tokens with flying.\n" +
        "Miracle {X}{W}{W} (You may cast this card for its miracle cost when you draw it if it's the " +
        "first card you drew this turn.)"

    spell {
        effect = Effects.CreateToken(
            count = DynamicAmount.XValue,
            power = 4,
            toughness = 4,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Angel"),
            keywords = setOf(Keyword.FLYING),
        )
    }

    keywordAbility(KeywordAbility.miracle("{X}{W}{W}"))

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "20"
        artist = "Todd Lockwood"
        imageUri = "https://cards.scryfall.io/normal/front/3/1/31292616-70e6-4d19-a883-e63ad860f50c.jpg?1783940735"
    }
}
