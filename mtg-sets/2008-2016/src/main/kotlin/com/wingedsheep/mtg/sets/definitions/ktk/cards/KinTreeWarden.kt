package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Kin-Tree Warden
 * {G}
 * Creature — Human Warrior
 * 1/1
 * {2}: Regenerate this creature.
 * Morph {G}
 */
val KinTreeWarden = card("Kin-Tree Warden") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Warrior"
    power = 1
    toughness = 1
    oracleText = "{2}: Regenerate this creature.\nMorph {G} (You may cast this card face down as a 2/2 creature for {3}. Turn it face up any time for its morph cost.)"

    activatedAbility {
        cost = Costs.Mana("{2}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    morph = "{G}"

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "139"
        artist = "Volkan Baǵa"
        flavorText = "\"The amber of the tree and the blood of my veins are the same.\""
        imageUri = "https://cards.scryfall.io/normal/front/d/f/dfddafbc-590b-4610-894a-b91335a60528.jpg?1562794779"
    }
}
