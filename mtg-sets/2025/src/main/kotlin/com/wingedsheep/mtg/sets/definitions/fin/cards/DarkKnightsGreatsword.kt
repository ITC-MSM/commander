package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.jobSelect
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GrantSubtype
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Dark Knight's Greatsword
 * {2}{B}
 * Artifact — Equipment
 * Job select (When this Equipment enters, create a 1/1 colorless Hero creature token,
 *   then attach this to it.)
 * Equipped creature gets +3/+0 and is a Knight in addition to its other types.
 * Chaosbringer — Equip—Pay 3 life. Activate only once each turn.
 *
 * "Chaosbringer" is the flavor name on the equip ability. The equip cost is a non-mana cost
 * ("Pay 3 life"), so it is modeled as an equip-flagged activated ability (isEquipAbility, so
 * it is sorcery-speed and eligible for equip-cost rules) with Costs.PayLife(3), restricted to
 * once per turn (ActivationRestriction.OncePerTurn), following Shredder's Armor's
 * "Equip—Sacrifice ... Activate only once each turn." pattern.
 */
val DarkKnightsGreatsword = card("Dark Knight's Greatsword") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Artifact — Equipment"
    oracleText = "Job select (When this Equipment enters, create a 1/1 colorless Hero creature token, then attach this to it.)\n" +
        "Equipped creature gets +3/+0 and is a Knight in addition to its other types.\n" +
        "Chaosbringer — Equip—Pay 3 life. Activate only once each turn."

    jobSelect()

    staticAbility {
        ability = ModifyStats(3, 0, Filters.EquippedCreature)
    }
    staticAbility {
        ability = GrantSubtype("Knight", Filters.EquippedCreature)
    }

    activatedAbility {
        isEquipAbility = true
        cost = Costs.PayLife(3)
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.AttachEquipment(creature)
        timing = TimingRule.SorcerySpeed
        restrictions = listOf(ActivationRestriction.OncePerTurn)
        description = "Chaosbringer — Equip—Pay 3 life. Activate only once each turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "95"
        artist = "Narendra Bintara Adi"
        imageUri = "https://cards.scryfall.io/normal/front/b/5/b50dcc7c-260f-4d8c-9a9e-9244ec23a91e.jpg?1748706120"
    }
}
