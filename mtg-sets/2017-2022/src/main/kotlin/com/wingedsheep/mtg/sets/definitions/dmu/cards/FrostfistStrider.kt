package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Frostfist Strider
 * {3}{U}{U}
 * Creature — Elemental Giant
 * 4/4
 * Ward {2} (Whenever this creature becomes the target of a spell or ability an opponent controls, counter it unless that player pays {2}.)
 * When this creature enters, tap target creature an opponent controls and put a stun counter on it. (If a permanent with a stun counter would become untapped, remove one from it instead.)
 */
val FrostfistStrider = card("Frostfist Strider") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Elemental Giant"
    oracleText = "Ward {2} (Whenever this creature becomes the target of a spell or ability an opponent controls, counter it unless that player pays {2}.)\nWhen this creature enters, tap target creature an opponent controls and put a stun counter on it. (If a permanent with a stun counter would become untapped, remove one from it instead.)"
    power = 4
    toughness = 4

    keywords(Keyword.WARD)
    keywordAbility(KeywordAbility.ward("{2}"))

    // A stun counter replaces the permanent's next untap, so the tap sticks for a turn cycle.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", TargetCreature(filter = TargetFilter.Creature.opponentControls()))
        effect = Effects.Composite(
            Effects.Tap(t),
            Effects.AddCounters(Counters.STUN, 1, t)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "51"
        artist = "Francisco Miyara"
        imageUri = "https://cards.scryfall.io/normal/front/4/0/40141353-c0d6-4529-b26a-34dfccbcf231.jpg?1783921351"
    }
}
