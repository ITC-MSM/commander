package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Obsidian Acolyte
 * {1}{W}
 * Creature — Human Cleric
 * 1/1
 * Protection from black
 * {W}: Target creature gains protection from black until end of turn.
 */
val ObsidianAcolyte = card("Obsidian Acolyte") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 1
    toughness = 1
    oracleText = "Protection from black\n{W}: Target creature gains protection from black until end of turn."

    keywordAbility(KeywordAbility.Protection(ProtectionScope.Color(Color.BLACK)))

    activatedAbility {
        cost = Costs.Mana("{W}")
        val t = target("target", Targets.Creature)
        effect = Effects.GrantProtectionFromColor(Color.BLACK, t)
        description = "{W}: Target creature gains protection from black until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "22"
        artist = "Matthew D. Wilson"
        imageUri = "https://cards.scryfall.io/normal/front/8/6/868efcee-bb13-4b6f-b81b-99408685e4c4.jpg?1562922212"
    }
}
