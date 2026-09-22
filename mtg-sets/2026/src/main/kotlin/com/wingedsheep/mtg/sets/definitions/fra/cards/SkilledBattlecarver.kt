package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val SkilledBattlecarver = card("Skilled Battlecarver") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Warrior"
    power = 2
    toughness = 1
    oracleText = "During your turn, this creature has first strike.\n{1}{R}: This creature gets +1/+0 until end of turn."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.FIRST_STRIKE, Filters.Self),
            condition = Conditions.IsYourTurn
        )
    }
    activatedAbility {
        cost = Costs.Mana("{1}{R}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
        description = "This creature gets +1/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "92"
        artist = "Evyn Fong"
        flavorText = "Konstrari frontline mages are adept at turning implements of craft into instruments of war."
        imageUri = "https://cards.scryfall.io/normal/front/0/e/0e44f959-1322-4abd-b6eb-dea992307c0c.jpg?1789556811"
        inBooster = false
    }
}
