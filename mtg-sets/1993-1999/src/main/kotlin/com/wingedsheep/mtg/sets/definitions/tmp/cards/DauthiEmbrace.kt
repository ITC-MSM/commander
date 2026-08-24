package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dauthi Embrace
 * {2}{B}
 * Enchantment
 * {B}{B}: Target creature gains shadow until end of turn. (It can block or be blocked by only creatures with shadow.)
 */
val DauthiEmbrace = card("Dauthi Embrace") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "{B}{B}: Target creature gains shadow until end of turn. (It can block or be blocked by only creatures with shadow.)"

    activatedAbility {
        cost = Costs.Mana("{B}{B}")
        val t = target("target", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.SHADOW, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "120"
        artist = "Andrew Robinson"
        flavorText = "\"The Dauthi army grows by screams and bounds.\"\n" +
            "—Lyna, Soltari emissary"
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7e84bb94-d654-4d69-89d9-0a398a940125.jpg"
    }
}
