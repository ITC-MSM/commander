package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Folk of the Pines
 * {4}{G}
 * Creature — Dryad
 * 2/5
 *
 * {1}{G}: This creature gets +1/+0 until end of turn.
 *
 * Firebreathing in green: a mana-only activated ability whose effect is `Effects.ModifyStats` onto
 * `EffectTarget.Self`, taking the facade's default `Duration.EndOfTurn`.
 */
val FolkOfThePines = card("Folk of the Pines") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dryad"
    power = 2
    toughness = 5
    oracleText = "{1}{G}: This creature gets +1/+0 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{1}{G}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "235"
        artist = "NéNé Thomas & Catherine Buck"
        flavorText = "\"Our friends of the forest take many forms, yet all serve the will of Freyalise.\"\n—Laina of the Elvish Council"
        imageUri = "https://cards.scryfall.io/normal/front/0/c/0c13311d-db83-483f-ba2b-4f54ceb8b026.jpg"
    }
}
