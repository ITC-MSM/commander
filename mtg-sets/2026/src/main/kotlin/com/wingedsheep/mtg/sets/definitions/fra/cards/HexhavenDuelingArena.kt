package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Hexhaven Dueling Arena — Reality Fracture #181
 * Land
 *
 * Both prepare abilities may target any creature (the {2} one only a creature that attacked this
 * turn); only a creature with a prepare spell can actually become prepared, which
 * [Effects.BecomePrepared] enforces at resolution — any other creature is a legal target and the
 * ability simply does nothing to it.
 */
val HexhavenDuelingArena = card("Hexhaven Dueling Arena") {
    manaCost = ""
    colorIdentity = ""
    typeLine = "Land"
    oracleText = "{T}: Add {C}.\n" +
        "{2}, {T}: Target creature that attacked this turn becomes prepared. Activate only as a sorcery. " +
        "(Only creatures with prepare spells can become prepared.)\n" +
        "{4}, {T}: Target creature becomes prepared."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap)
        val creature = target(
            "target creature that attacked this turn",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.attackedThisTurn()))
        )
        effect = Effects.BecomePrepared(creature)
        timing = TimingRule.SorcerySpeed
        description = "{2}, {T}: Target creature that attacked this turn becomes prepared. Activate only as a sorcery."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}"), Costs.Tap)
        val creature = target("target creature", Targets.Creature)
        effect = Effects.BecomePrepared(creature)
        description = "{4}, {T}: Target creature becomes prepared."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "181"
        artist = "Constantin Marin"
        flavorText = "Those who survive are stronger for it."
        imageUri = "https://cards.scryfall.io/normal/front/9/1/9128ce00-6744-4d36-bfbe-ef75d78110b0.jpg?1789556985"
        inBooster = false
    }
}
