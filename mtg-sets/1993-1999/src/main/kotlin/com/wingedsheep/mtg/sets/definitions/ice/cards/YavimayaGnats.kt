package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Yavimaya Gnats
 * {2}{G}
 * Creature — Insect
 * 0/1
 *
 * Flying
 * {G}: Regenerate this creature.
 *
 * A printed keyword plus the shared [RegenerateEffect] on [EffectTarget.Self] behind a plain mana
 * cost — no new vocabulary.
 */
val YavimayaGnats = card("Yavimaya Gnats") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Insect"
    power = 0
    toughness = 1
    oracleText = "Flying\n" +
        "{G}: Regenerate this creature."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{G}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "280"
        artist = "Dan Frazier"
        flavorText = "\"It is our third day of travel on the Yavimaya River, and still these creatures plague us. Davin Lansson, our naturalist, has facetiously labeled them 'gnats,' and the name has stuck.\"\n—Disa the Restless, journal entry"
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9d8b7020-ca8f-4867-bc51-13d824daf154.jpg"
    }
}
