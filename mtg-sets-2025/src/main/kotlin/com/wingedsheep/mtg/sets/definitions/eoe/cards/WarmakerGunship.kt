package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantCardType
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.station

/**
 * Warmaker Gunship
 * {2}{R}
 * Artifact — Spacecraft
 * 4/3
 *
 * When this Spacecraft enters, it deals damage equal to the number of artifacts you control
 * to target creature an opponent controls.
 * Station (Tap another creature you control: Put charge counters equal to its power on this
 * Spacecraft. Station only as a sorcery. It's an artifact creature at 6+.)
 * 6+ | Flying
 */
val WarmakerGunship = card("Warmaker Gunship") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Artifact — Spacecraft"
    power = 4
    toughness = 3
    oracleText = "When this Spacecraft enters, it deals damage equal to the number of artifacts you " +
        "control to target creature an opponent controls.\n" +
        "Station (Tap another creature you control: Put charge counters equal to its power on this " +
        "Spacecraft. Station only as a sorcery. It's an artifact creature at 6+.)\n6+ | Flying"

    // When this Spacecraft enters, it deals damage equal to the number of artifacts you control
    // to target creature an opponent controls.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target creature an opponent controls", Targets.CreatureOpponentControls)
        effect = Effects.DealDamage(
            amount = DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Artifact),
            target = t,
            damageSource = EffectTarget.Self
        )
        description = "When this Spacecraft enters, it deals damage equal to the number of artifacts " +
            "you control to target creature an opponent controls."
    }

    // Station activated ability: tap another creature → add charge counters equal to its power
    station()

    // Conditional type change: artifact creature at 6+ charge counters
    staticAbility {
        condition = Conditions.SourceCounterCountAtLeast(Counters.CHARGE, 6)
        ability = GrantCardType("CREATURE", GroupFilter.source())
    }

    // Conditional keyword: flying at 6+ charge counters
    staticAbility {
        condition = Conditions.SourceCounterCountAtLeast(Counters.CHARGE, 6)
        ability = GrantKeyword(Keyword.FLYING.name, GroupFilter.source())
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "167"
        artist = "Julian Kok Joon Wen"
        imageUri = "https://cards.scryfall.io/normal/front/9/e/9e5957f4-1cae-4989-8b40-27fc6e2fcf5e.jpg?1755341273"
    }
}
