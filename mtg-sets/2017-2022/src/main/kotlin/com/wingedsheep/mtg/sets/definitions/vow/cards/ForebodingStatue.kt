package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.TransformEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Foreboding Statue // Forsaken Thresher (Innistrad: Crimson Vow)
 * {3}
 * Artifact Creature — Construct // Artifact Creature — Construct
 *
 * Front — Foreboding Statue (1/2)
 *   {T}: Add one mana of any color. Put an omen counter on this creature.
 *   At the beginning of your end step, if there are three or more omen counters on this creature,
 *   untap it, then transform it.
 *
 * Back — Forsaken Thresher (5/5)
 *   At the beginning of your first main phase, add one mana of any color.
 *
 * The activated ability is still a **mana ability** (CR 605.1a): it could add mana, it targets
 * nothing, and it is not a loyalty ability — the omen-counter rider does not disqualify it. So it
 * carries `manaAbility = true` / [TimingRule.ManaAbility] and resolves without using the stack,
 * with the counter riding along in the same [Effects.Composite]. Consequence, and the release-note
 * ruling: because the end-step trigger untaps the Statue only on *resolution*, you cannot tap it
 * for one more mana while that trigger is resolving.
 *
 * The end-step clause is a true intervening "if" (CR 603.4) on `interveningIf`, so a Statue that
 * loses its third counter in response never resolves the untap-and-flip.
 */

private val ForebodingStatueFront = card("Foreboding Statue") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Construct"
    power = 1
    toughness = 2
    oracleText = "{T}: Add one mana of any color. Put an omen counter on this creature.\n" +
        "At the beginning of your end step, if there are three or more omen counters on this " +
        "creature, untap it, then transform it."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.Composite(
            Effects.AddAnyColorMana(1),
            Effects.AddCounters(Counters.OMEN, 1, EffectTarget.Self)
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
        description = "{T}: Add one mana of any color. Put an omen counter on this creature."
    }

    triggeredAbility {
        trigger = Triggers.YourEndStep
        interveningIf = Conditions.SourceCounterCountAtLeast(Counters.OMEN, 3)
        effect = Effects.Untap(EffectTarget.Self) then TransformEffect(EffectTarget.Self)
        description = "At the beginning of your end step, if there are three or more omen counters " +
            "on this creature, untap it, then transform it."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "256"
        artist = "Aaron Miller"
        flavorText = "As the sand eroded, a faint sound could be heard whirling under the ground."
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a27b9d82-f613-4789-9e8b-f37db5597027.jpg?1783924791"

        ruling(
            "2021-11-19",
            "You can't tap Foreboding Statue to get one more mana while resolving its triggered " +
                "ability in your end step."
        )
    }
}

private val ForsakenThresher = card("Forsaken Thresher") {
    manaCost = ""
    colorIdentity = ""
    typeLine = "Artifact Creature — Construct"
    power = 5
    toughness = 5
    oracleText = "At the beginning of your first main phase, add one mana of any color."

    triggeredAbility {
        trigger = Triggers.FirstMainPhase
        effect = Effects.AddAnyColorMana(1)
        description = "At the beginning of your first main phase, add one mana of any color."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "256"
        artist = "Aaron Miller"
        flavorText = "Were it not for the cultists' fatal curiosity, the terrible machine might " +
            "have lain dormant for decades more."
        imageUri = "https://cards.scryfall.io/normal/back/a/2/a27b9d82-f613-4789-9e8b-f37db5597027.jpg?1783924791"
    }
}

val ForebodingStatue: CardDefinition = CardDefinition.doubleFacedCreature(
    frontFace = ForebodingStatueFront,
    backFace = ForsakenThresher,
)
