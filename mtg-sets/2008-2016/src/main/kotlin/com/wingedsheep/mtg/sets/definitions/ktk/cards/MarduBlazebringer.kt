package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect
import com.wingedsheep.sdk.core.Step

/**
 * Mardu Blazebringer
 * {2}{R}
 * Creature — Ogre Warrior
 * 4/4
 * When Mardu Blazebringer attacks or blocks, sacrifice it at end of combat.
 *
 * One printed sentence, two triggered abilities: a creature can never both attack and block in one
 * combat, so the disjunction fires at most once either way, and this is the spelling the corpus
 * writes 60 times against three for `EventPattern.AnyOf`. The sacrifice is `SacrificeSelfEffect` —
 * the verb with no object, which reads the delayed trigger's own source — rather than
 * `SacrificeTarget(Self)`; the two are behaviourally identical and Argentum Assay reads the printed
 * line as the former, so the card carries the spelling the grammar prints.
 */
val MarduBlazebringer = card("Mardu Blazebringer") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Ogre Warrior"
    power = 4
    toughness = 4
    oracleText = "When Mardu Blazebringer attacks or blocks, sacrifice it at end of combat."

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = CreateDelayedTriggerEffect(
                step = Step.END_COMBAT,
                effect = SacrificeSelfEffect
            )
    }

    triggeredAbility {
        trigger = Triggers.Blocks
        effect = CreateDelayedTriggerEffect(
                step = Step.END_COMBAT,
                effect = SacrificeSelfEffect
            )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "115"
        artist = "Peter Mohrbacher"
        flavorText = "\"Make sure he's pointed in the right direction before you light him. And don't let the goblins anywhere near the torch.\" —Kerai Suddenblade, Mardu hordechief"
        imageUri = "https://cards.scryfall.io/normal/front/c/b/cbf7a797-f32a-4ed2-b835-a356120f5817.jpg?1562793597"
    }
}
