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
 * Knight of Stromgald
 * {B}{B}
 * Creature — Human Knight
 * 2/1
 *
 * Protection from white
 * {B}: This creature gains first strike until end of turn.
 * {B}{B}: This creature gets +1/+0 until end of turn.
 *
 * Functionally identical to [com.wingedsheep.mtg.sets.definitions.fem.cards.OrderOfTheEbonHand], so
 * it reuses that shape exactly: protection is the structured [KeywordAbility.Protection] with a
 * [ProtectionScope.Color] rather than a bare `keywords(...)` entry, and the two activations are
 * plain mana-cost [Effects.GrantKeyword] / [Effects.ModifyStats] on [EffectTarget.Self].
 */
val KnightOfStromgald = card("Knight of Stromgald") {
    manaCost = "{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Knight"
    power = 2
    toughness = 1
    oracleText = "Protection from white\n" +
        "{B}: This creature gains first strike until end of turn.\n" +
        "{B}{B}: This creature gets +1/+0 until end of turn."

    keywordAbility(KeywordAbility.Protection(ProtectionScope.Color(Color.WHITE)))

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = Effects.GrantKeyword(Keyword.FIRST_STRIKE, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Mana("{B}{B}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "138"
        artist = "Mark Poole"
        flavorText = "\"Kjeldorans should rule supreme, and to the rest, death!\"\n—Avram Garrisson, Leader of the Knights of Stromgald"
        imageUri = "https://cards.scryfall.io/normal/front/2/b/2b87069b-ebaf-4705-b5da-446932af9b73.jpg"
    }
}
