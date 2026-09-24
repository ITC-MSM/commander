package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Roiling Canopy (Reality Fracture #187) — Land.
 *
 * "Other" in the intervening "if" is relative to the Forest that entered, not to the Canopy
 * (which isn't a Forest itself), so it is
 * [Conditions.YouControlAtLeastOtherThanTriggering] rather than a count against six: if the
 * entering Forest has left by the time the ability resolves, five remaining Forests still satisfy
 * the recheck.
 */
val RoilingCanopy = card("Roiling Canopy") {
    manaCost = ""
    colorIdentity = "G"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n" +
        "Whenever a Forest you control enters, if you control at least five other Forests, target " +
        "creature you control gets +3/+3 until end of turn.\n" +
        "{T}: Add {G}."

    replacementEffect(EntersTapped())

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = Filters.ForestCard.youControl(),
            binding = TriggerBinding.ANY
        )
        interveningIf = Conditions.YouControlAtLeastOtherThanTriggering(5, Filters.ForestCard)
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.ModifyStats(3, 3, creature)
        description = "Whenever a Forest you control enters, if you control at least five other Forests, " +
            "target creature you control gets +3/+3 until end of turn."
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "187"
        artist = "Titus Lunter"
        flavorText = "Jace left slices of his personality trapped in his strongest memories."
        imageUri = "https://cards.scryfall.io/normal/front/d/b/db61361b-bd12-453e-abc2-bbe09b66e3d9.jpg?1789514074"
        inBooster = false
    }
}
