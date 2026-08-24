package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Esper Battlemage
 * {2}{U}
 * Artifact Creature — Human Wizard
 * 2 / 2
 * {W}, {T}: Prevent the next 2 damage that would be dealt to you this turn.
 * {B}, {T}: Target creature gets -1/-1 until end of turn.
 *
 * The Alara battlemage cycle: two off-colour activated abilities sharing one tap. Both costs are a
 * [Costs.Composite] of a single coloured pip and [Costs.Tap], so only one may be used per turn.
 * "Dealt to you" is [Effects.PreventNextDamage] pointed at [EffectTarget.Controller] — a shield with
 * an amount rather than a scope, so it soaks any 2 damage from any source, combat or not, and needs
 * no target requirement. The black half is a plain [Effects.ModifyStats] whose `-1/-1` runs for the
 * default end-of-turn duration.
 */
val EsperBattlemage = card("Esper Battlemage") {
    manaCost = "{2}{U}"
    colorIdentity = "BUW"
    typeLine = "Artifact Creature — Human Wizard"
    power = 2
    toughness = 2
    oracleText = "{W}, {T}: Prevent the next 2 damage that would be dealt to you this turn.\n" +
        "{B}, {T}: Target creature gets -1/-1 until end of turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W}"), Costs.Tap)
        effect = Effects.PreventNextDamage(2, EffectTarget.Controller)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{B}"), Costs.Tap)
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.ModifyStats(-1, -1, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "40"
        artist = "Matt Cavotta"
        flavorText = "She can heal the flesh, or exploit its many weaknesses."
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2cac0076-8bd5-4fe2-bac8-fd102688fdcb.jpg"
    }
}
