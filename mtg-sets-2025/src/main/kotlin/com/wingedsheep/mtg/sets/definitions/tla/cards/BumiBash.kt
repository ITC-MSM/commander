package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Bumi Bash
 * {3}{R}
 * Sorcery
 * Choose one —
 * • Bumi Bash deals damage equal to the number of lands you control to target creature.
 * • Destroy target land creature or nonbasic land.
 *
 * A true "Choose one —" modal spell ([ModalEffect.chooseOne], countsAsModalSpell = true).
 * Mode 1 is dynamic burn: damage equals [DynamicAmounts.landsYouControl] to a target creature
 * (the spell is the default damage source). Mode 2 destroys a target that is either a land
 * creature (a creature that is also a land) or a nonbasic land — modeled as a [TargetFilter]
 * with two alternatives.
 */
val BumiBash = card("Bumi Bash") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Choose one —\n" +
        "• Bumi Bash deals damage equal to the number of lands you control to target creature.\n" +
        "• Destroy target land creature or nonbasic land."

    spell {
        effect = ModalEffect.chooseOne(
            Mode(
                effect = Effects.DealDamage(
                    DynamicAmounts.landsYouControl(),
                    EffectTarget.ContextTarget(0),
                ),
                targetRequirements = listOf(TargetCreature()),
                description = "Bumi Bash deals damage equal to the number of lands you control to target creature",
            ),
            Mode(
                effect = Effects.Destroy(EffectTarget.ContextTarget(0)),
                targetRequirements = listOf(
                    TargetObject(
                        filter = TargetFilter(GameObjectFilter.Creature and GameObjectFilter.Land)
                            .or(TargetFilter.NonbasicLand),
                        id = "target land creature or nonbasic land",
                    ),
                ),
                description = "Destroy target land creature or nonbasic land",
            ),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "125"
        artist = "Maël Ollivier-Henry"
        flavorText = "\"You thought I was a frail old man, but I'm the most powerful Earthbender you'll ever see.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/8/e8895479-26cb-4ee8-98ca-6d46c43f0dbd.jpg?1773973552"
    }
}
