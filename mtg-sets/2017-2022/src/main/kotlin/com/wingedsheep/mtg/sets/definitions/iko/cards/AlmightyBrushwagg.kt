package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Almighty Brushwagg — Ikoria: Lair of Behemoths #143
 * {G} · Creature — Brushwagg · 1/1
 *
 * Trample
 * {3}{G}: This creature gets +3/+3 until end of turn.
 *
 * A pure mana sink: the pump targets nothing, it modifies the source itself
 * ([EffectTarget.Self]), so it can be activated repeatedly and each activation stacks its own
 * until-end-of-turn Layer 7c modification. Trample is what turns the extra power into damage
 * once a chump blocker is in the way.
 */
val AlmightyBrushwagg = card("Almighty Brushwagg") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Brushwagg"
    power = 1
    toughness = 1
    oracleText = "Trample\n{3}{G}: This creature gets +3/+3 until end of turn."

    keywords(Keyword.TRAMPLE)

    activatedAbility {
        cost = Costs.Mana("{3}{G}")
        effect = Effects.ModifyStats(3, 3, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "143"
        artist = "Dmitry Burmak"
        flavorText = "Laughed at the brushwagg\n—Hunters' expression meaning \"died unexpectedly\""
        imageUri = "https://cards.scryfall.io/normal/front/7/1/71f2b7ac-8742-468d-b6a3-87881cb522ff.jpg"
    }
}
