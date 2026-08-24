package com.wingedsheep.mtg.sets.definitions.bfz.cards

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
 * Zulaport Cutthroat
 * {1}{B}
 * Creature — Human Rogue Ally
 * 1/1
 * Whenever this creature or another creature you control dies, each opponent loses 1 life and you gain 1 life.
 *
 * ANY binding, because the printed line says "this creature **or** another creature you
 * control" — the Cutthroat's own death has to fire it too.
 */
val ZulaportCutthroat = card("Zulaport Cutthroat") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Rogue Ally"
    power = 1
    toughness = 1
    oracleText = "Whenever this creature or another creature you control dies, each opponent loses 1 life and you " +
        "gain 1 life."

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.youControl(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY,
        )
        effect = Effects.Composite(
            Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent)),
            Effects.GainLife(1),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "126"
        artist = "Jason Rainville"
        flavorText = "\"Eldrazi? Ha! Try walking through Zulaport at night with your pockets full. Now *that's* " +
            "dangerous.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5c963d97-c948-45d9-84cc-58e246d35970.jpg?1783938198"
    }
}
