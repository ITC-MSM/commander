package com.wingedsheep.mtg.sets.definitions.eld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Locthwain Gargoyle
 * {1}
 * Artifact Creature — Gargoyle
 * 0/3
 * {4}: This creature gets +2/+0 and gains flying until end of turn.
 *
 * A repeatable self-pump: one activated ability whose effect composes the stat
 * modification with the flying grant, both on [EffectTarget.Self] and both with the
 * default end-of-turn duration. Re-activating while it already has flying is legal
 * and simply stacks another +2/+0.
 */
val LocthwainGargoyle = card("Locthwain Gargoyle") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Gargoyle"
    power = 0
    toughness = 3
    oracleText = "{4}: This creature gets +2/+0 and gains flying until end of turn."

    activatedAbility {
        cost = Costs.Mana("{4}")
        effect = Effects.Composite(
            Effects.ModifyStats(2, 0, EffectTarget.Self),
            Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "225"
        artist = "Nils Hamm"
        flavorText = "\"As the traitor hurried across the courtyard, its eyes snapped open. It was time to pay the queen a visit.\" —*Beyond the Great Henge*"
        imageUri = "https://cards.scryfall.io/normal/front/0/2/02551bee-335c-4bf7-b38e-67dd71d1d567.jpg?1783932583"
        ruling("2019-10-04", "You can activate Locthwain Gargoyle's ability multiple times during one turn, even if it already has flying.")
    }
}
