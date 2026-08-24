package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Order of the White Shield
 * {W}{W}
 * Creature — Human Knight
 * 2/1
 *
 * Protection from black
 * {W}: This creature gains first strike until end of turn.
 * {W}{W}: This creature gets +1/+0 until end of turn.
 *
 * The white mirror of [KnightOfStromgald], and functionally identical to
 * [com.wingedsheep.mtg.sets.definitions.fem.cards.OrderOfLeitbur]: protection is the structured
 * [KeywordAbility.Protection] with a [ProtectionScope.Color], and both activations are plain
 * mana-cost effects on [EffectTarget.Self].
 */
val OrderOfTheWhiteShield = card("Order of the White Shield") {
    manaCost = "{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    power = 2
    toughness = 1
    oracleText = "Protection from black\n" +
        "{W}: This creature gains first strike until end of turn.\n" +
        "{W}{W}: This creature gets +1/+0 until end of turn."

    keywordAbility(KeywordAbility.Protection(ProtectionScope.Color(Color.BLACK)))

    activatedAbility {
        cost = Costs.Mana("{W}")
        effect = Effects.GrantKeyword(Keyword.FIRST_STRIKE, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Mana("{W}{W}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "46"
        artist = "Ruth Thompson"
        flavorText = "\"Shall we turn away a worthy soul because his parents were peasants? I think not.\"\n—Lucilde Fiksdotter, Leader of the Order of the White Shield"
        imageUri = "https://cards.scryfall.io/normal/front/9/2/92e55b10-375f-4b4f-b676-3b9b8085fdd2.jpg"
    }
}
