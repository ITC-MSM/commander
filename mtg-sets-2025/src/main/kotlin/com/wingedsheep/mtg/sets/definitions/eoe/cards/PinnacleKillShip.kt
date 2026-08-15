package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantCardType
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.station

/**
 * Pinnacle Kill-Ship
 * {7}
 * Artifact — Spacecraft
 * When this Spacecraft enters, it deals 10 damage to up to one target creature.
 * Station (Tap another creature you control: Put charge counters equal to its power on this Spacecraft. Station only as a sorcery. It's an artifact creature at 7+.)
 * 7+ | Flying
 * 7/7
 */
val PinnacleKillShip = card("Pinnacle Kill-Ship") {
    manaCost = "{7}"
    colorIdentity = ""
    typeLine = "Artifact — Spacecraft"
    power = 7
    toughness = 7
    oracleText = "When this Spacecraft enters, it deals 10 damage to up to one target creature.\nStation (Tap another creature you control: Put charge counters equal to its power on this Spacecraft. Station only as a sorcery. It's an artifact creature at 7+.)\n7+ | Flying"

    // ETB: deals 10 damage to up to one target creature
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val target = target("up to one target creature", TargetCreature(optional = true))
        effect = Effects.DealDamage(10, target)
    }

    // Station activated ability: tap another creature → add charge counters equal to its power
    station()

    // 7+ charge counters: becomes artifact creature and gains flying
    val charge7 = Conditions.SourceCounterCountAtLeast(Counters.CHARGE, 7)

    staticAbility {
        condition = charge7
        ability = GrantCardType("CREATURE", GroupFilter.source())
    }

    staticAbility {
        condition = charge7
        ability = GrantKeyword(Keyword.FLYING, GroupFilter.source())
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "244"
        artist = "Alexandre Honoré"
        imageUri = "https://cards.scryfall.io/normal/front/b/f/bf784de8-5ae2-4c07-92bb-a5b7f593b773.jpg?1755341417"
    }
}
