package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.targets.TargetObject

val FateshaperAspirant = card("Fateshaper Aspirant") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Rhino Cleric"
    power = 3
    toughness = 4
    oracleText = "When this creature enters, choose one —\n" +
        "• Return target legendary card from your graveyard to your hand.\n" +
        "• Put a +1/+1 counter on target creature. It gains vigilance and indestructible until end of turn. " +
        "(Damage and effects that say \"destroy\" don't destroy it.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ModalEffect.chooseOne(
            // "Legendary card" is any card type — not narrowed to permanents or creatures.
            Mode.withTarget(
                Effects.Move(EffectTarget.ContextTarget(0), Zone.HAND),
                TargetObject(filter = TargetFilter.CardInGraveyard.legendary().ownedByYou()),
                "Return target legendary card from your graveyard to your hand."
            ),
            Mode.withTarget(
                Effects.Composite(
                    Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.ContextTarget(0)),
                    Effects.GrantKeyword(Keyword.VIGILANCE, EffectTarget.ContextTarget(0)),
                    Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, EffectTarget.ContextTarget(0))
                ),
                TargetCreature(),
                "Put a +1/+1 counter on target creature. It gains vigilance and indestructible until end of turn."
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "6"
        artist = "Paul Dainton"
        imageUri = "https://cards.scryfall.io/normal/front/f/0/f0c8400d-824f-4d79-84bc-7615a0deb831.jpg?1789556683"
        inBooster = false
    }
}
