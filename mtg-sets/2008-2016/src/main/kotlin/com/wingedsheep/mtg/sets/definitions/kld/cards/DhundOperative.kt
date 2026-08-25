package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Dhund Operative
 * {1}{B}
 * Creature — Human Rogue
 * 2/2
 * As long as you control an artifact, this creature gets +1/+0 and has deathtouch.
 *
 * One printed sentence, two static abilities: the pump and the keyword grant are separate layers
 * (7c and 6), so each is its own [ConditionalStaticAbility] over the same
 * [Conditions.ControlArtifact]. `GroupFilter.source()` scopes both to this creature.
 */
val DhundOperative = card("Dhund Operative") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Rogue"
    oracleText = "As long as you control an artifact, this creature gets +1/+0 and has deathtouch."
    power = 2
    toughness = 2

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(
                powerBonus = 1,
                toughnessBonus = 0,
                filter = GroupFilter.source()
            ),
            condition = Conditions.ControlArtifact
        )
    }

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.DEATHTOUCH, GroupFilter.source()),
            condition = Conditions.ControlArtifact
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "74"
        artist = "Magali Villeneuve"
        flavorText = "Baral's spies always have the latest technology."
        imageUri = "https://cards.scryfall.io/normal/front/9/8/98f91094-5214-4268-8f91-5ba0b891256d.jpg?1783937211"
    }
}
