package com.wingedsheep.mtg.sets.definitions.atq.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration

/**
 * Phyrexian Gremlins
 * {2}{B}
 * Creature — Phyrexian Gremlin
 * 1/1
 * You may choose not to untap this creature during your untap step.
 * {T}: Tap target artifact. It doesn't untap during its controller's untap step for as long as
 * this creature remains tapped.
 *
 * Imposed untap suppression. The {T} ability taps a target artifact ([Effects.Tap]) and grants it
 * [AbilityFlag.DOESNT_UNTAP] via [Effects.GrantKeyword] with the [Duration.WhileSourceTapped]
 * duration: while Phyrexian Gremlins remains tapped the artifact carries "doesn't untap", so it is
 * filtered out of *its own controller's* untap step (the untap step honors DOESNT_UNTAP for whoever
 * is untapping). The instant Gremlins untaps, the one-way `WhileSourceTapped` latch removes the
 * grant and the artifact untaps normally on its controller's next untap step. Holding Gremlins
 * tapped through your untap step ([AbilityFlag.MAY_NOT_UNTAP]) keeps the lock alive.
 */
val PhyrexianGremlins = card("Phyrexian Gremlins") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Phyrexian Gremlin"
    power = 1
    toughness = 1
    oracleText = "You may choose not to untap this creature during your untap step.\n" +
        "{T}: Tap target artifact. It doesn't untap during its controller's untap step for as long " +
        "as this creature remains tapped."

    flags(AbilityFlag.MAY_NOT_UNTAP)

    activatedAbility {
        cost = Costs.Tap
        val artifact = target("target artifact", Targets.Artifact)
        effect = Effects.Tap(artifact) then
            Effects.GrantKeyword(AbilityFlag.DOESNT_UNTAP, artifact, Duration.WhileSourceTapped("Phyrexian Gremlins"))
        description = "{T}: Tap target artifact. It doesn't untap during its controller's untap step " +
            "for as long as this creature remains tapped."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "18"
        artist = "Amy Weber"
        imageUri = "https://cards.scryfall.io/normal/front/2/1/21a985a9-5612-4844-982e-fd1aa6249770.jpg?1562902131"
    }
}
