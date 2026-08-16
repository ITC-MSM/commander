package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Air Marshal
 * {1}{U}
 * Creature — Human Soldier
 * 2/1
 * {3}: Target Soldier gains flying until end of turn.
 *
 * "Soldier" is a **bare tribal noun**, so it names every *permanent* with the subtype rather than
 * only a creature — [TargetFilter.Permanent] with the subtype, not `TargetFilter.Creature`.
 */
val AirMarshal = card("Air Marshal") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 1
    oracleText = "{3}: Target Soldier gains flying until end of turn."

    activatedAbility {
        cost = Costs.Mana("{3}")
        val t = target("target", TargetPermanent(filter = TargetFilter.Permanent.withSubtype("Soldier")))
        effect = Effects.GrantKeyword(Keyword.FLYING, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "43"
        artist = "Johan Grenier"
        flavorText = "\"Today our blades remember Kroog! May the seven brass gods be with us!\""
        imageUri = "https://cards.scryfall.io/normal/front/9/9/991e1a6c-914b-45c8-9170-c09e72696117.jpg?1783920115"
    }
}
