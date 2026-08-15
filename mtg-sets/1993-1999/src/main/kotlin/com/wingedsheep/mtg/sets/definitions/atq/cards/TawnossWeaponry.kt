package com.wingedsheep.mtg.sets.definitions.atq.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration

/**
 * Tawnos's Weaponry
 * {2}
 * Artifact
 * You may choose not to untap this artifact during your untap step.
 * {2}, {T}: Target creature gets +1/+1 for as long as this artifact remains tapped.
 *
 * A "tap-locked" buff (cf. Everglove Courier): the +1/+1 is a [Effects.ModifyStats] with the
 * [Duration.WhileSourceTapped] duration, so it lasts only while Tawnos's Weaponry stays tapped and
 * drops the instant it untaps. The optional self untap-skip is the [AbilityFlag.MAY_NOT_UNTAP]
 * flag, letting the controller hold it tapped through their untap step to keep the bonus alive.
 */
val TawnossWeaponry = card("Tawnos's Weaponry") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "You may choose not to untap this artifact during your untap step.\n" +
        "{2}, {T}: Target creature gets +1/+1 for as long as this artifact remains tapped."

    flags(AbilityFlag.MAY_NOT_UNTAP)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap)
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(1, 1, creature, Duration.WhileSourceTapped("Tawnos's Weaponry"))
        description = "{2}, {T}: Target creature gets +1/+1 for as long as this artifact remains tapped."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "70"
        artist = "Dan Frazier"
        flavorText = "When Urza's war machines became too costly, Tawnos's weaponry replaced them."
        imageUri = "https://cards.scryfall.io/normal/front/3/0/3035cead-a501-4204-9154-5fd648577d32.jpg?1562905158"
    }
}
