package com.wingedsheep.mtg.sets.definitions.big.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.effects.GrantKeywordEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sandstorm Salvager — The Big Score #19
 * {2}{G} · Creature — Human Artificer · Mythic
 * 1/1
 *
 * When this creature enters, create a 3/3 colorless Golem artifact creature token.
 * {2}, {T}: Put a +1/+1 counter on each creature token you control. They gain trample
 * until end of turn.
 *
 * The activated ability iterates the projected battlefield via [Effects.ForEachInGroup]
 * over "creature tokens you control" ([GameObjectFilter.Creature.youControl().token]) and
 * places a +1/+1 counter on each, then grants trample (default end-of-turn duration) to that
 * same set — so only the creature tokens it counters gain trample, per the oracle text.
 */
val SandstormSalvager = card("Sandstorm Salvager") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Artificer"
    power = 1
    toughness = 1
    oracleText = "When this creature enters, create a 3/3 colorless Golem artifact creature token.\n" +
        "{2}, {T}: Put a +1/+1 counter on each creature token you control. They gain trample " +
        "until end of turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = CreateTokenEffect(
            power = 3,
            toughness = 3,
            colors = emptySet(),
            creatureTypes = setOf("Golem"),
            artifactToken = true,
            imageUri = "https://cards.scryfall.io/normal/front/4/0/406e2960-f560-48bb-b4a6-4bd35889a8f8.jpg?1712318018"
        )
        description = "When this creature enters, create a 3/3 colorless Golem artifact creature token."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap)
        description = "{2}, {T}: Put a +1/+1 counter on each creature token you control. They " +
            "gain trample until end of turn."
        val creatureTokensYouControl = GroupFilter(GameObjectFilter.Creature.youControl().token())
        effect = Effects.Composite(
            Effects.ForEachInGroup(
                filter = creatureTokensYouControl,
                effect = AddCountersEffect(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
            ),
            Effects.ForEachInGroup(
                filter = creatureTokensYouControl,
                effect = GrantKeywordEffect(Keyword.TRAMPLE, EffectTarget.Self)
            )
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "19"
        artist = "Francis Tneh"
        flavorText = "Mora didn't want friends, but she didn't mind having someone to watch her back."
        imageUri = "https://cards.scryfall.io/normal/front/1/3/13b0f27c-a359-4702-833a-82fec161eeec.jpg?1739804214"
    }
}
