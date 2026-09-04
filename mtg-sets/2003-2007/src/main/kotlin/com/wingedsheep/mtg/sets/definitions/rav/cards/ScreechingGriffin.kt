package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Screeching Griffin
 * {3}{W}
 * Creature — Griffin
 * 2/2
 * Flying
 * {R}: Target creature can't block this creature this turn.
 *
 * The restriction is *pairwise* — the target is still free to block anything else, so this is
 * [Effects.CantBlock] with `attacker = EffectTarget.Self` (the Griffin) rather than the blanket
 * "can't block" the same effect spells without it.
 */
val ScreechingGriffin = card("Screeching Griffin") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Griffin"
    oracleText = "Flying\n{R}: Target creature can't block this creature this turn."
    power = 2
    toughness = 2
    keywords(Keyword.FLYING)
    activatedAbility {
        cost = Costs.Mana("{R}")
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.CantBlock(target = t, attacker = EffectTarget.Self)
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "29"
        artist = "Greg Hildebrandt"
        flavorText = "They were master fishers, but their seas are now the streets and their catch, the goblins that run pell-mell from the screeching in the sky."
        imageUri = "https://cards.scryfall.io/normal/front/5/e/5e2fa710-4615-42de-8716-41b009b56d32.jpg?1783943695"
    }
}
