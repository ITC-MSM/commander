package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Wose Pathfinder
 * {1}{G}
 * Creature — Human Shaman
 * 1/1
 *
 * {T}: Add one mana of any color.
 * {6}{G}, {T}: Another target creature gets +3/+3 and gains trample until end of turn.
 */
val WosePathfinder = card("Wose Pathfinder") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Shaman"
    power = 1
    toughness = 1
    oracleText = "{T}: Add one mana of any color.\n{6}{G}, {T}: Another target creature gets +3/+3 and gains trample until end of turn."

    // {T}: Add one mana of any color.
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    // {6}{G}, {T}: Another target creature gets +3/+3 and gains trample until end of turn.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{6}{G}"), Costs.Tap)
        val other = target("another target creature", TargetCreature(filter = TargetFilter.OtherCreature))
        effect = Effects.ModifyStats(3, 3, other)
            .then(Effects.GrantKeyword(Keyword.TRAMPLE, other))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "190"
        artist = "Iga Oliwiak"
        flavorText = "\"Road is forgotten, but not by Wild Folk. Over hill and behind hill it lies still under grass and tree. Wild Folk will show you that road.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/5/6589b339-9067-4e9b-bfdb-c49f8b3ef2d4.jpg?1686969622"
    }
}
