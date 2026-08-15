package com.wingedsheep.mtg.sets.definitions.atq.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration

/**
 * Ashnod's Battle Gear
 * {2}
 * Artifact
 * You may choose not to untap this artifact during your untap step.
 * {2}, {T}: Target creature you control gets +2/-2 for as long as this artifact remains tapped.
 *
 * A "tap-locked" buff (cf. Tawnos's Weaponry, Everglove Courier): the +2/-2 is a
 * [Effects.ModifyStats] with [Duration.WhileSourceTapped], lasting only while Battle Gear stays
 * tapped and ending when it untaps. The optional self untap-skip is [AbilityFlag.MAY_NOT_UNTAP].
 * Note the modern oracle restricts the target to a creature you control.
 */
val AshnodsBattleGear = card("Ashnod's Battle Gear") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "You may choose not to untap this artifact during your untap step.\n" +
        "{2}, {T}: Target creature you control gets +2/-2 for as long as this artifact remains tapped."

    flags(AbilityFlag.MAY_NOT_UNTAP)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap)
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.ModifyStats(2, -2, creature, Duration.WhileSourceTapped("Ashnod's Battle Gear"))
        description = "{2}, {T}: Target creature you control gets +2/-2 for as long as this artifact remains tapped."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "39"
        artist = "Mark Poole"
        flavorText = "This horrid invention clearly illustrates why Mishra's lieutenant was feared as much by her troops as by her foes."
        imageUri = "https://cards.scryfall.io/normal/front/a/e/aeeec853-dd3f-4ac3-8b20-c07fada8888f.jpg?1562931933"
    }
}
