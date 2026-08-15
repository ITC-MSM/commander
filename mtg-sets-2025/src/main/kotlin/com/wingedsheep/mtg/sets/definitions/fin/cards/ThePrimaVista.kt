package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * The Prima Vista
 * {4}{U}
 * Legendary Artifact — Vehicle
 * 5/3
 * Flying
 * Whenever you cast a noncreature spell, if at least four mana was spent to cast it, The Prima
 * Vista becomes an artifact creature until end of turn.
 * Crew 2 (Tap any number of creatures you control with total power 2 or more: This Vehicle becomes
 * an artifact creature until end of turn.)
 *
 * "If at least four mana was spent to cast it" is an intervening-if on the cast trigger (CR 603.4),
 * modeled by [Conditions.TriggeringSpellManaSpentAtLeast] reading the triggering spell's recorded
 * total mana paid (so {X} spells that paid four or more qualify, while a four-mana-value spell cast
 * for less does not). The payoff animates the source via [Effects.BecomeCreature] on
 * [EffectTarget.Self] with `Duration.EndOfTurn`: it keeps its Artifact type and Vehicle subtype and
 * simply gains the CREATURE type with its printed 5/3 — the same animation Crew produces.
 */
val ThePrimaVista = card("The Prima Vista") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Artifact — Vehicle"
    oracleText = "Flying\n" +
        "Whenever you cast a noncreature spell, if at least four mana was spent to cast it, The " +
        "Prima Vista becomes an artifact creature until end of turn.\n" +
        "Crew 2 (Tap any number of creatures you control with total power 2 or more: This Vehicle " +
        "becomes an artifact creature until end of turn.)"
    power = 5
    toughness = 3
    keywords(Keyword.FLYING)

    // Whenever you cast a noncreature spell, if at least four mana was spent to cast it,
    // The Prima Vista becomes an artifact creature until end of turn.
    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        triggerCondition = Conditions.TriggeringSpellManaSpentAtLeast(4)
        effect = Effects.BecomeCreature(
            target = EffectTarget.Self,
            power = 5,
            toughness = 3,
            duration = Duration.EndOfTurn,
        )
    }

    keywordAbility(KeywordAbility.crew(2))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "64"
        artist = "Leon Tukker"
        imageUri = "https://cards.scryfall.io/normal/front/e/3/e3998132-5746-4dde-9529-97d3ad7d7361.jpg?1748705994"
    }
}
