package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Kor Entanglers
 * {4}{W}
 * Creature — Kor Soldier Ally
 * 3/4
 * Rally — Whenever this creature or another Ally you control enters, tap target creature an opponent controls.
 *
 * Rally is an ability word, not a keyword: the trigger is an ANY-bound enters trigger over
 * Allies you control, so the creature's own arrival fires it alongside every later Ally.
 */
val KorEntanglers = card("Kor Entanglers") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kor Soldier Ally"
    power = 3
    toughness = 4
    oracleText = "Rally — Whenever this creature or another Ally you control enters, tap target creature an " +
        "opponent controls."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype("Ally").youControl(),
            binding = TriggerBinding.ANY,
        )
        val creature = target("target creature", Targets.CreatureOpponentControls)
        effect = Effects.Tap(creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "36"
        artist = "Jason Rainville"
        flavorText = "\"We came into this world together. We fight for this world together. We'll leave this world " +
            "together.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/0/0039ead5-2afa-49d6-ae4a-45ae2118188a.jpg?1783938218"
    }
}
