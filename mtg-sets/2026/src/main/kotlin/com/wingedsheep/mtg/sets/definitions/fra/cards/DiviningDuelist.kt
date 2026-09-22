package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val DiviningDuelist = card("Divining Duelist") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Wizard"
    power = 3
    toughness = 2
    oracleText = "Flash\nWhen this creature enters, choose one —\n• Tap target creature.\n• Untap target creature.\n• Draw a card, then discard a card."

    keywords(Keyword.FLASH)
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ModalEffect.chooseOne(
            Mode.withTarget(Effects.Tap(EffectTarget.ContextTarget(0)), Targets.Creature, "Tap target creature."),
            Mode.withTarget(Effects.Untap(EffectTarget.ContextTarget(0)), Targets.Creature, "Untap target creature."),
            Mode(effect = Effects.Composite(Effects.DrawCards(1), Effects.Discard(1)), description = "Draw a card, then discard a card.")
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "29"
        artist = "David Auden Nash"
        flavorText = "\"I predicted your quips yesterday, Stingerquill. They weren't clever then either.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/6/960c7335-331d-488b-be68-2ad1c1c695dc.jpg?1789556709"
        inBooster = false
    }
}
