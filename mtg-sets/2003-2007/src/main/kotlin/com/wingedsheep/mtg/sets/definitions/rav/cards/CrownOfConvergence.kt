package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.RevealTopOfLibrary
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.EntityReference

val CrownOfConvergence = card("Crown of Convergence") {
    manaCost = "{2}"
    colorIdentity = "GW"
    typeLine = "Artifact"
    oracleText = "Play with the top card of your library revealed.\nAs long as the top card of your library is a creature card, creatures you control that share a color with that card get +1/+1.\n{G}{W}: Put the top card of your library on the bottom of your library."

    staticAbility { ability = RevealTopOfLibrary }

    staticAbility {
        condition = Conditions.EntityMatches(EffectTarget.LibraryTop(), GameObjectFilter.Creature)
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(GameObjectFilter.Creature.youControl()
                .sharingColorWith(EntityReference.LibraryTop()))
        )
    }

    activatedAbility {
        cost = Costs.Mana("{G}{W}")
        effect = Effects.PutOnBottomOfLibrary(EffectTarget.LibraryTop())
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "258"
        artist = "Jen Page"
        imageUri = "https://cards.scryfall.io/normal/front/c/1/c1b9f8c8-2927-4746-a540-dd3853b9a00e.jpg?1783943600"
        ruling("2013-04-15", "The top card of your library isn't in your hand, so you can't suspend it, cycle it, discard it, or activate any of its activated abilities.")
        ruling("2013-04-15", "If the top card of your library changes while you're casting a spell, playing a land, or activating an ability, the new top card won't be revealed until you finish doing so.")
        ruling("2013-04-15", "When playing with the top card of your library revealed, if an effect tells you to draw several cards, reveal each one before you draw it.")
        ruling("2005-10-01", "A colorless creature on top of your library never gives a bonus, and a colorless creature on the battlefield never gets a bonus.")
    }
}
