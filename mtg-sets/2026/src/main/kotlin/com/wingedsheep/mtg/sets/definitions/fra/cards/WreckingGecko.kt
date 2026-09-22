package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.WardCost
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val WreckingGecko = card("Wrecking Gecko") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Artifact Creature — Lizard Construct"
    power = 5
    toughness = 5
    oracleText = "Ward {2} (Whenever this creature becomes the target of a spell or ability an opponent controls, counter it unless that player pays {2}.)\n{6}{G}{G}: This creature gets +4/+4 and gains trample until end of turn."

    keywordAbility(KeywordAbility.Ward(WardCost.Mana("{2}")))
    activatedAbility {
        cost = Costs.Mana("{6}{G}{G}")
        effect = Effects.Composite(
            Effects.ModifyStats(4, 4, EffectTarget.Self),
            Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "120"
        artist = "Raph Lomotan"
        flavorText = "\"Corridors 17-24 are now open for extra credit training. Come prepared.\"\n—Campus announcement"
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3d693cb0-681e-480a-8f70-07e94c39225c.jpg?1789385843"
        inBooster = false
    }
}
