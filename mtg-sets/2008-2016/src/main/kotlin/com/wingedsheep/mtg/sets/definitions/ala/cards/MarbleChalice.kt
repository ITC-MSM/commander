package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Marble Chalice
 * {2}{W}
 * Artifact
 * {T}: You gain 1 life.
 *
 * One of Shards of Alara's coloured artifacts. A bare [Costs.Tap] activated ability whose effect is
 * [Effects.GainLife] — "you" is the ability's controller, which is already the facade's default
 * target, so no `EffectTarget` is spelled. It is not a mana ability, so it uses the ordinary
 * sorcery-speed-free activation timing and goes on the stack.
 */
val MarbleChalice = card("Marble Chalice") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Artifact"
    oracleText = "{T}: You gain 1 life."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "18"
        artist = "Howard Lyon"
        flavorText = "The cup was a gift from the sphinx Tameron, who hoped that those who drank from it would live long enough to decrypt the sphinxes' wisdom."
        imageUri = "https://cards.scryfall.io/normal/front/b/7/b76e9580-9154-476b-923f-b23bf55db026.jpg"
    }
}
