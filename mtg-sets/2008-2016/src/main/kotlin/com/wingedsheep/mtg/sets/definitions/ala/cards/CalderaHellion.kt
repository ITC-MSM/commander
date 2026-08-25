package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDevour
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Caldera Hellion
 * {3}{R}{R}
 * Creature — Hellion
 * 3 / 3
 * Devour 1 (As this creature enters, you may sacrifice any number of creatures. It enters with that many +1/+1 counters on it.)
 * When this creature enters, it deals 3 damage to each creature.
 *
 * Devour is two declarations, not one: [KeywordAbility.devour] gives the printed line (and the
 * keyword the rest of the engine reads), while the [EntersWithDevour] replacement effect is what
 * actually offers the sacrifice and stamps the +1/+1 counters as the creature enters — its defaults
 * are the plain creature filter and the unnamed variant, exactly the printed "devour 1". The sweep
 * clause is a plain [Triggers.EntersBattlefield] over
 * [Effects.ForEachInGroup] of every creature — including the Hellion itself, which is why it usually
 * eats its own devour counters — with [DealDamageEffect] bound to [EffectTarget.Self], the
 * per-iteration member.
 */
val CalderaHellion = card("Caldera Hellion") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Hellion"
    power = 3
    toughness = 3
    oracleText = "Devour 1 (As this creature enters, you may sacrifice any number of creatures. It enters with that many +1/+1 counters on it.)\n" +
        "When this creature enters, it deals 3 damage to each creature."

    keywords(Keyword.DEVOUR)
    keywordAbility(KeywordAbility.devour(1))

    replacementEffect(EntersWithDevour(multiplier = 1))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature),
            DealDamageEffect(3, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "95"
        artist = "Raymond Swanland"
        imageUri = "https://cards.scryfall.io/normal/front/8/8/88866ce7-0b81-4b6c-a17e-ce5e51a0f2da.jpg"
    }
}
