package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Wily Bandar
 * {G}
 * Creature — Cat Monkey
 * 1/1
 * {2}{G}: This creature gains indestructible until end of turn.
 *
 * A mana-only activated ability granting [Keyword.INDESTRUCTIBLE] to [EffectTarget.Self] for the
 * default `Duration.EndOfTurn`.
 */
val WilyBandar = card("Wily Bandar") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Cat Monkey"
    power = 1
    toughness = 1
    oracleText = "{2}{G}: This creature gains indestructible until end of turn. (Damage and effects that say \"destroy\" don't destroy it.)"

    activatedAbility {
        cost = Costs.Mana("{2}{G}")
        effect = Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, EffectTarget.Self)
        description = "This creature gains indestructible until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "175"
        artist = "Yeong-Hao Han"
        flavorText = "Part feline, part primate, all trouble."
        imageUri = "https://cards.scryfall.io/normal/front/c/a/cac6ece3-9889-47fa-9140-420b4f31dd1b.jpg?1783937171"
    }
}
