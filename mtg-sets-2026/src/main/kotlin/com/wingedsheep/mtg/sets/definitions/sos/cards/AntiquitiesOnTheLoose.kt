package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Antiquities on the Loose — Secrets of Strixhaven #7
 * {1}{W}{W} · Sorcery
 *
 * Create two 2/2 red and white Spirit creature tokens. Then if this spell was cast from anywhere
 * other than your hand, put a +1/+1 counter on each Spirit you control.
 * Flashback {4}{W}{W}.
 *
 * "Cast from anywhere other than your hand" is `Conditions.Not(Conditions.WasCastFromHand)` — true
 * for the flashback (graveyard) cast and any other non-hand origin, false for a normal hand cast.
 * The gated payoff fans the +1/+1 counter over every Spirit you control via `Effects.ForEachInGroup`
 * (which snapshots the group first, so the two just-made Spirits are included). Flashback is the
 * stock `KeywordAbility.flashback`.
 */
val AntiquitiesOnTheLoose = card("Antiquities on the Loose") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Create two 2/2 red and white Spirit creature tokens. Then if this spell was cast " +
        "from anywhere other than your hand, put a +1/+1 counter on each Spirit you control.\n" +
        "Flashback {4}{W}{W} (You may cast this card from your graveyard for its flashback cost. " +
        "Then exile it.)"

    spell {
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.RED, Color.WHITE),
            creatureTypes = setOf("Spirit"),
            count = 2,
            imageUri = "https://cards.scryfall.io/normal/front/8/7/877f7ddb-ed70-41a0-b845-d9bf8ac65f9b.jpg?1775828448"
        ) then ConditionalEffect(
            condition = Conditions.Not(Conditions.WasCastFromHand),
            effect = Effects.ForEachInGroup(
                filter = GroupFilter(GameObjectFilter.Creature.youControl().withSubtype("Spirit")),
                effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
            )
        )
    }

    keywordAbility(KeywordAbility.flashback("{4}{W}{W}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "7"
        artist = "Andreas Zafiratos"
        flavorText = "Raising spirits is easier than taming them."
        imageUri = "https://cards.scryfall.io/normal/front/6/8/68ee92cd-51af-4de5-bcc8-34d0bb2fd398.jpg?1775936960"
    }
}
