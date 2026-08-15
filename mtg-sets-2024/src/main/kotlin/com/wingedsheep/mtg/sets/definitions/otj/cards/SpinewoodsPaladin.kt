package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Spinewoods Paladin
 * {4}{G}
 * Creature — Human Knight
 * 5/4
 *
 * Trample
 * When this creature enters, you gain 3 life.
 * Plot {3}{G} (You may pay {3}{G} and exile this card from your hand. Cast it as a sorcery
 * on a later turn without paying its mana cost. Plot only as a sorcery.)
 */
val SpinewoodsPaladin = card("Spinewoods Paladin") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Knight"
    power = 5
    toughness = 4
    oracleText = "Trample\nWhen this creature enters, you gain 3 life.\n" +
        "Plot {3}{G} (You may pay {3}{G} and exile this card from your hand. Cast it as a sorcery " +
        "on a later turn without paying its mana cost. Plot only as a sorcery.)"

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(3)
        description = "you gain 3 life."
    }

    keywordAbility(KeywordAbility.plot("{3}{G}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "183"
        artist = "Kai Carpenter"
        imageUri = "https://cards.scryfall.io/normal/front/1/b/1b2b432d-9e73-4ab2-a098-546d406df6c0.jpg?1712356003"
    }
}
