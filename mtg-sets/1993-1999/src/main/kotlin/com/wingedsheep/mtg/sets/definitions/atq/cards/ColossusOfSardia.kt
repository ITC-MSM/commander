package com.wingedsheep.mtg.sets.definitions.atq.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Colossus of Sardia
 * {9}
 * Artifact Creature — Golem
 * 9/9
 * Trample
 * This creature doesn't untap during your untap step.
 * {9}: Untap this creature. Activate only during your upkeep.
 *
 * "Doesn't untap" is the mandatory self-suppression flag [AbilityFlag.DOESNT_UNTAP] (cf. Goblin
 * Sharpshooter) — the untap step filters it out, so it stays tapped after attacking. The pay-to-untap
 * ability is a plain [Effects.Untap] on itself, gated to the controller's upkeep via
 * [ActivationRestriction.OnlyDuringYourTurn] + [ActivationRestriction.DuringStep] UPKEEP (cf.
 * Dwarven Weaponsmith).
 */
val ColossusOfSardia = card("Colossus of Sardia") {
    manaCost = "{9}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Golem"
    power = 9
    toughness = 9
    oracleText = "Trample\nThis creature doesn't untap during your untap step.\n" +
        "{9}: Untap this creature. Activate only during your upkeep."

    keywords(Keyword.TRAMPLE)
    flags(AbilityFlag.DOESNT_UNTAP)

    activatedAbility {
        cost = Costs.Mana("{9}")
        effect = Effects.Untap(EffectTarget.Self)
        restrictions = listOf(
            ActivationRestriction.All(
                ActivationRestriction.OnlyDuringYourTurn,
                ActivationRestriction.DuringStep(Step.UPKEEP)
            )
        )
        description = "{9}: Untap this creature. Activate only during your upkeep."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "46"
        artist = "Jesper Myrfors"
        flavorText = "From the Sardian mountains wakes ancient doom: Warrior born from a rocky womb."
        imageUri = "https://cards.scryfall.io/normal/front/0/6/067c44e9-1b23-42fd-9acb-daafb62c32a2.jpg?1562896386"
    }
}
