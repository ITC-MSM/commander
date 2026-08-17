package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Bonecaller Cleric
 * {1}{B}
 * Creature — Human Cleric
 * 2/1
 * {3}{B}, Sacrifice this creature: Return target creature card from your graveyard to the battlefield. Activate only as a sorcery.
 *
 * Broodheart Engine's reanimation shape on a creature: a [Costs.Composite] of mana plus
 * [Costs.SacrificeSelf], and [Effects.Move] out of the graveyard onto the battlefield. "Activate
 * only as a sorcery" is a [TimingRule], not an activation restriction — a different field and a
 * different code path from "only during your turn".
 */
val BonecallerCleric = card("Bonecaller Cleric") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Cleric"
    power = 2
    toughness = 1
    oracleText = "{3}{B}, Sacrifice this creature: Return target creature card from your graveyard to the battlefield. Activate only as a sorcery."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{B}"), Costs.SacrificeSelf)
        val creature = target("target", Targets.CreatureCardInYourGraveyard)
        effect = Effects.Move(creature, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD)
        timing = TimingRule.SorcerySpeed
        description = "{3}{B}, Sacrifice this creature: Return target creature card from your " +
            "graveyard to the battlefield. Activate only as a sorcery."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "117"
        artist = "Jason A. Engle"
        flavorText = "The bell's unearthly chime slips into the ears of the dead, waking them rudely from their eternal slumber."
        imageUri = "https://cards.scryfall.io/normal/front/8/7/87f411d0-c796-443f-b957-c632aa23deb0.jpg?1783922765"
    }
}
