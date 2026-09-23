package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Mind Meanderer — vigilance is not printed, so it is a [ConditionalStaticAbility] over the source
 * keyed to "a Jace planeswalker" (the planeswalker filter narrowed by subtype, as Arisen Gorgon
 * does for Liliana). The fight target is optional ("up to one"), so the trigger still resolves
 * with no target chosen.
 */
val MindMeanderer = card("Mind Meanderer") {
    manaCost = "{3}{G}{U}{U}"
    colorIdentity = "GU"
    typeLine = "Creature — Bird Fish Illusion"
    power = 4
    toughness = 4
    oracleText = "Flying\n" +
        "This creature has vigilance as long as you control a Jace planeswalker.\n" +
        "When this creature enters, it fights up to one target creature an opponent controls. " +
        "(Each deals damage equal to its power to the other.)"

    keywords(Keyword.FLYING)

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.VIGILANCE, GroupFilter.source()),
            condition = Conditions.YouControl(GameObjectFilter.Planeswalker.withSubtype("Jace"))
        )
    }

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val foe = target(
            "up to one target creature an opponent controls",
            TargetCreature(optional = true, filter = TargetFilter.CreatureOpponentControls),
        )
        effect = Effects.Fight(EffectTarget.Self, foe)
        description = "When this creature enters, it fights up to one target creature an opponent controls."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "141"
        artist = "Jason Mowry"
        imageUri = "https://cards.scryfall.io/normal/front/9/4/94c290ce-252c-42b3-bcb0-c1ef621df566.jpg?1789470861"
        inBooster = false
    }
}
