package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ChoiceType
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.EntersWithChoice
import com.wingedsheep.sdk.scripting.TimingRule

val RoomOfRefuge = card("Room of Refuge") {
    manaCost = ""
    colorIdentity = ""
    typeLine = "Land"
    oracleText = "This land enters tapped. As it enters, choose a color.\n" +
        "{T}: Add one mana of the chosen color.\n" +
        "{5}, {T}, Sacrifice this land: Put two +1/+1 counters on target creature. Activate only as a sorcery."

    replacementEffect(EntersTapped())
    replacementEffect(EntersWithChoice(ChoiceType.COLOR))

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaOfChosenColor()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{5}"), Costs.Tap, Costs.SacrificeSelf)
        val creature = target("target creature", Targets.Creature)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, creature)
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "188"
        artist = "Constantin Marin"
        flavorText = "Any shelter from the Theorist's gaze was a welcome one."
        imageUri = "https://cards.scryfall.io/normal/front/9/a/9a467560-6676-4fc2-9400-768a79650aa4.jpg?1789557010"
        inBooster = false
    }
}
