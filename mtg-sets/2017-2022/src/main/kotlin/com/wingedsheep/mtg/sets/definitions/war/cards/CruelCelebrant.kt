package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Cruel Celebrant — War of the Spark #188 (canonical printing)
 * {W}{B}
 * Creature — Vampire
 * 1/2
 * Whenever this creature or another creature or planeswalker you control dies, each opponent
 * loses 1 life and you gain 1 life.
 *
 * "This creature **or another**" is not two triggers: it is one [TriggerBinding.ANY] trigger
 * whose filter already admits the source — the same spelling [Triggers] uses for
 * `YourCreatureDies`. The only reason it goes through [Triggers.leavesBattlefield] rather than
 * that constant is the permanent type: Cruel Celebrant also counts planeswalkers, so the filter
 * is [GameObjectFilter.CreatureOrPlaneswalker] narrowed with `youControl()`. Spelled out at the
 * call site rather than promoted to a `Triggers` constant — there is exactly one card in the
 * corpus with this shape so far.
 */
val CruelCelebrant = card("Cruel Celebrant") {
    manaCost = "{W}{B}"
    colorIdentity = "WB"
    typeLine = "Creature — Vampire"
    oracleText = "Whenever this creature or another creature or planeswalker you control dies, " +
        "each opponent loses 1 life and you gain 1 life."
    power = 1
    toughness = 2

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.CreatureOrPlaneswalker.youControl(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY
        )
        effect = Effects.Composite(
            Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent)),
            Effects.GainLife(1)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "188"
        artist = "Bastien L. Deharme"
        flavorText = "\"Whichever side wins, I'm sure the banquet will be superb.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/7/87ead6ac-b1c5-4852-8413-7fa43c6cfc57.jpg"
    }
}
