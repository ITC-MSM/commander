package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Barrier Breach — Ikoria: Lair of Behemoths #145
 * {2}{G} · Instant
 *
 * Exile up to three target enchantments.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 *
 * "Up to three target" is one requirement with `count = 3, optional = true`, so zero targets is a
 * legal choice and the spell still resolves. The exile is fanned out with [ForEachTargetEffect] so
 * each chosen enchantment is moved independently — one illegal target on resolution doesn't take
 * the others with it.
 */
val BarrierBreach = card("Barrier Breach") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Exile up to three target enchantments.\n" +
        "Cycling {2} ({2}, Discard this card: Draw a card.)"

    spell {
        target("target", TargetPermanent(count = 3, optional = true, filter = TargetFilter.Enchantment))
        effect = ForEachTargetEffect(
            effects = listOf(Effects.Exile(EffectTarget.ContextTarget(0)))
        )
    }

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "145"
        artist = "Mathias Kollros"
        flavorText = "\"They cast wards, thinking it will protect them, but the biggest monsters find a way through.\"\n—Kinnan, bonder prodigy"
        imageUri = "https://cards.scryfall.io/normal/front/8/2/822f8403-77a7-4e75-88af-60d604632f5d.jpg"
    }
}
