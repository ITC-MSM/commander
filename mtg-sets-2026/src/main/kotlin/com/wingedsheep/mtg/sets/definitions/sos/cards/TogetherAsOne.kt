package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Together as One
 * {6}
 * Sorcery
 *
 * Converge — Target player draws X cards, Together as One deals X damage to any target, and you gain
 * X life, where X is the number of colors of mana spent to cast this spell.
 *
 * A three-part Converge spell. X = [DynamicAmounts.colorsOfManaSpent]
 * (`DynamicAmount.DistinctColorsManaSpent`), resolved while the spell is still on the stack so it
 * reads the live per-colour payment buckets (same as Snarl Song / Arcane Omens). The two targets are
 * declared in printed order — *target player* first, *any target* second — and the three effects are
 * chained with `.then(...)`: the target player draws X, Together as One deals X to the any-target
 * (damage source = the spell itself), and the controller gains X life. With an all-colourless {6}
 * payment X = 0: the player draws nothing, no damage is dealt, and no life is gained — the spell
 * still resolves and its targets are still chosen at cast time.
 */
val TogetherAsOne = card("Together as One") {
    manaCost = "{6}"
    colorIdentity = ""
    typeLine = "Sorcery"
    oracleText = "Converge — Target player draws X cards, Together as One deals X damage to any " +
        "target, and you gain X life, where X is the number of colors of mana spent to cast this spell."

    spell {
        val player = target("target player", Targets.Player)
        val anyTarget = target("any target", Targets.Any)
        effect = Effects.DrawCards(DynamicAmounts.colorsOfManaSpent(), player)
            .then(
                Effects.DealDamage(
                    DynamicAmounts.colorsOfManaSpent(),
                    anyTarget,
                    damageSource = EffectTarget.Self,
                ),
            )
            .then(Effects.GainLife(DynamicAmounts.colorsOfManaSpent()))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "4"
        artist = "Néstor Ossandón Leal"
        flavorText = "By himself, Lluwen couldn't have hoped to calm the Dawning Archaic—but any " +
            "time it really mattered, he was never alone."
        imageUri = "https://cards.scryfall.io/normal/front/a/c/ac2a8a66-e38c-42ab-83e1-d2d99ee48861.jpg?1775936940"
    }
}
