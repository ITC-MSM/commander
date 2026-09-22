package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val WinterTeamPlayer = card("Winter, Team Player") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Warrior"
    oracleText = "Convoke\nWhenever you cast a noncreature spell, creatures you control get +1/+0 until end of turn."
    power = 3
    toughness = 3

    keywords(Keyword.CONVOKE)

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = Effects.ForEachInGroup(GroupFilter.AllCreaturesYouControl, Effects.ModifyStats(1, 0, EffectTarget.Self))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "256"
        artist = "Aurore Folny"
        flavorText = "\"Everyone stick together. Follow my lead!\""
        imageUri = "https://cards.scryfall.io/normal/front/d/f/df8713cd-3f4b-43ef-adbd-e37c2617c617.jpg?1789128020"
        inBooster = false
    }
}
