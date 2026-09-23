package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

val DivinerOfVictory = card("Diviner of Victory") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Dwarf Wizard"
    power = 1
    toughness = 1
    oracleText = "This creature enters prepared. (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)\nWhenever you scry or surveil, this creature gets +1/+1 until end of turn."

    keywords(Keyword.PREPARED)

    triggeredAbility {
        trigger = Triggers.WheneverYouScryOrSurveil
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
        description = "Whenever you scry or surveil, this creature gets +1/+1 until end of turn."
    }

    prepare("Unwind History") {
        manaCost = "{1}{U}"
        typeLine = "Sorcery"
        oracleText = "Return target creature an opponent controls with mana value 3 or less to its owner's hand. Surveil 1."
        spell {
            val creature = target(
                "target creature an opponent controls with mana value 3 or less",
                TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.opponentControls().manaValueAtMost(3)))
            )
            effect = Effects.ReturnToHand(creature)
                .then(Patterns.Library.surveil(1))
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "28"
        artist = "Justyna Dura"
        imageUri = "https://cards.scryfall.io/normal/front/0/8/0853bb80-8664-432a-8457-600139fd96d5.jpg?1788878145"
        inBooster = false
    }
}
