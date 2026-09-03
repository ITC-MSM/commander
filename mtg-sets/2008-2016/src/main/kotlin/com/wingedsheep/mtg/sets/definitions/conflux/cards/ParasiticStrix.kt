package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Parasitic Strix
 * {2}{U}
 * Artifact Creature — Bird
 * 2 / 2
 * Flying
 * When this creature enters, if you control a black permanent, target player loses 2 life and you gain 2 life.
 *
 * Conflux's "domain of a colour" clause is an ordinary intervening-"if" (CR 603.4), so it is
 * checked both when the Strix enters and again on resolution — losing the last black permanent in
 * response fizzles the drain. [Conditions.YouControl] over `Permanent.withColor(BLACK)` is the
 * existential the JSON names `Exists(You, Battlefield, …)`; the bare noun "permanent" is
 * deliberately not narrowed to creatures. The drain is one [Effects.Composite] of a targeted
 * [Effects.LoseLife] and an untargeted [Effects.GainLife], whose controller default is already you.
 */
val ParasiticStrix = card("Parasitic Strix") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Artifact Creature — Bird"
    power = 2
    toughness = 2
    oracleText = "Flying\n" +
        "When this creature enters, if you control a black permanent, target player loses 2 life and you gain 2 life."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        interveningIf = Conditions.YouControl(GameObjectFilter.Permanent.withColor(Color.BLACK))
        val victim = target("target", Targets.Player)
        effect = Effects.Composite(
            Effects.LoseLife(2, victim),
            Effects.GainLife(2)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "32"
        artist = "Steven Belledin"
        flavorText = "After finding no sustenance on the edges of Grixis, it turned to the skies of Bant."
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a36cd0c3-1955-41b1-9a9c-b30b31a2f094.jpg"
    }
}
