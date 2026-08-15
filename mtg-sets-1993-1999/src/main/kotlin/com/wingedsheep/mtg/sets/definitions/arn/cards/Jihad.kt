package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ChoiceType
import com.wingedsheep.sdk.scripting.EntersWithChoice
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Jihad
 * {W}{W}{W}
 * Enchantment
 *
 * As this enchantment enters, choose a color and an opponent.
 * White creatures get +2/+1 as long as the chosen player controls a nontoken
 * permanent of the chosen color.
 * When the chosen player controls no nontoken permanents of the chosen color,
 * sacrifice this enchantment.
 */
val Jihad = card("Jihad") {
    manaCost = "{W}{W}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "As this enchantment enters, choose a color and an opponent.\n" +
        "White creatures get +2/+1 as long as the chosen player controls a nontoken " +
        "permanent of the chosen color.\n" +
        "When the chosen player controls no nontoken permanents of the chosen color, " +
        "sacrifice this enchantment."

    replacementEffect(EntersWithChoice(ChoiceType.COLOR))
    replacementEffect(EntersWithChoice(ChoiceType.OPPONENT))

    staticAbility {
        ability = ModifyStats(
            powerBonus = 2,
            toughnessBonus = 1,
            filter = GroupFilter(GameObjectFilter.Creature.withColor(Color.WHITE))
        )
        condition = Exists(
            player = Player.ChosenOpponent,
            zone = Zone.BATTLEFIELD,
            filter = GameObjectFilter.Permanent.sharingChosenColorWithSource().nontoken()
        )
    }

    stateTriggeredAbility {
        condition = Exists(
            player = Player.ChosenOpponent,
            zone = Zone.BATTLEFIELD,
            filter = GameObjectFilter.Permanent.sharingChosenColorWithSource().nontoken(),
            negate = true
        )
        effect = Effects.SacrificeTarget(EffectTarget.Self)
        description = "When the chosen player controls no nontoken permanents of " +
            "the chosen color, sacrifice this enchantment"
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "5"
        artist = "Brian Snõddy"
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b6c7705a-2987-4ef1-92b1-2c55d989ec6f.jpg?1740685879"
    }
}
