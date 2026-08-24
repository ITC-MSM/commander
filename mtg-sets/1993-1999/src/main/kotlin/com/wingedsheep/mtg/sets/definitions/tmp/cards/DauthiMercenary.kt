package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dauthi Mercenary
 * {2}{B}
 * Creature — Dauthi Knight Mercenary
 * 2/1
 * Shadow (This creature can block or be blocked by only creatures with shadow.)
 * {1}{B}: This creature gets +1/+0 until end of turn.
 */
val DauthiMercenary = card("Dauthi Mercenary") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Dauthi Knight Mercenary"
    power = 2
    toughness = 1
    oracleText = "Shadow (This creature can block or be blocked by only creatures with shadow.)\n" +
        "{1}{B}: This creature gets +1/+0 until end of turn."

    keywords(Keyword.SHADOW)

    activatedAbility {
        cost = Costs.Mana("{1}{B}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "124"
        artist = "Matthew D. Wilson"
        flavorText = "\"The Dauthi believe they dignify murder by paying for it.\"\n" +
            "—Lyna, Soltari emissary"
        imageUri = "https://cards.scryfall.io/normal/front/c/3/c340e779-c648-48fd-a159-174b46f2d1b3.jpg"
    }
}
