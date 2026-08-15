package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Putrid Pals
 * {2}{B/G}{B/G}
 * Creature — Human Ooze Mutant
 * 3/3
 *
 * Deathtouch
 * Disappear — This creature enters with two +1/+1 counters on it if a permanent
 * left the battlefield under your control this turn.
 *
 * Modeled with the engine's standard conditional "enters with counters" idiom
 * (Benalish Lancer): an EntersBattlefield trigger gated by the Disappear
 * intervening-if that places the two counters.
 */
val PutridPals = card("Putrid Pals") {
    manaCost = "{2}{B/G}{B/G}"
    colorIdentity = "BG"
    typeLine = "Creature — Human Ooze Mutant"
    oracleText = "Deathtouch\nDisappear — This creature enters with two +1/+1 counters on it if a permanent left the battlefield under your control this turn."
    power = 3
    toughness = 3

    keywords(Keyword.DEATHTOUCH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.YouHadPermanentLeaveBattlefieldThisTurn
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self)
        description = "Disappear — This creature enters with two +1/+1 counters on it if a permanent left the battlefield under your control this turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "165"
        artist = "Kevin Sidharta"
        flavorText = "\"Beats working in the sewers. Too many freaky mutants down there.\"\n—Muckman"
        imageUri = "https://cards.scryfall.io/normal/front/4/2/42cdb88d-675a-43bb-b85c-51c4cc526315.jpg?1771587057"
    }
}
