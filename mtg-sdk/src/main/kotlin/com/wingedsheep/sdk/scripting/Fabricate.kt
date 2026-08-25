package com.wingedsheep.sdk.scripting

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Fabricate N (CR 702.123) as pure data — the enters-the-battlefield ability every fabricate
 * permanent has and none of them prints as a separate line.
 *
 * A card carrying `Fabricate 2` shows one keyword line plus its reminder text; the rules give it
 * one triggered ability, which CR 702.123a spells as a consent gate:
 *
 * > "Fabricate N" means "When this permanent enters, you may put N +1/+1 counters on it. If you
 * > don't, create N 1/1 colorless Servo artifact creature tokens."
 *
 * So [etbChoice] is a [MayEffect] with an `otherwise` — the shape [TriggeredAbility.effect]'s own
 * documentation names for a printed "you may … If you don't, …" — and **not** a
 * [com.wingedsheep.sdk.scripting.effects.ModalEffect]. The reminder line's "put two +1/+1 counters
 * on it **or** create two Servos" reads like a modal, and the two would perform identically once
 * resolved, but they are decided at different *times*: a top-level `ModalEffect` on a triggered
 * ability is a modal ability, and CR 603.3c has its controller lock the mode in as the ability is
 * **put onto the stack**. Fabricate is not modal — its choice is made on **resolution**, which is
 * exactly the fabricate play pattern the timing exists for: the opponent kills the creature in
 * response to the trigger, and only then do you decide to take Servos instead of counters. A
 * gate's decision runs at resolution (CR 608.2c), so that works.
 *
 * Unlike vanishing's N-free triggers ([Vanishing.upkeepCountdown]), fabricate's single trigger
 * *carries* N, so [etbChoice] is a factory rather than a singleton. And unlike vanishing, whose
 * multiple instances stack into one pile of time counters (CR 702.62d), **CR 702.123b makes each
 * instance of fabricate trigger separately** — so [printedCounts] returns one N per printed
 * instance and the engine builds one trigger each, rather than summing. A permanent with
 * fabricate 1 and fabricate 2 gets two independent decisions, not a single fabricate 3.
 *
 * The keyword is one of the "read N off the printed [KeywordAbility.Numeric]" family: the engine
 * gates the derivation on the *projected* keyword (so a "loses all abilities" effect strips it) but
 * reads N from the printed keyword ability, because a projected keyword set carries no parameter.
 * No corpus card, token, or effect grants fabricate to something that doesn't print it, so the two
 * sources never disagree today; if one ever does, a *granted* fabricate has no N to read and
 * derives nothing — the same limitation vanishing's granted case has for its entry counters.
 */
object Fabricate {

    /** Ability id prefix; the printed instance's index is appended so multiple instances differ. */
    private const val ABILITY_ID_PREFIX = "fabricate"

    /**
     * CR 702.123a — the fabricate trigger for a single printed `Fabricate [n]`.
     *
     * @param n the N of that instance.
     * @param instance the zero-based index of this instance among the permanent's printed
     *   fabricate abilities. It only distinguishes the [AbilityId]s, so that a permanent with two
     *   instances (CR 702.123b) puts two distinct abilities on the stack rather than two copies of
     *   one id.
     */
    fun etbChoice(n: Int, instance: Int = 0): TriggeredAbility {
        val counterWord = if (n == 1) "counter" else "counters"
        val tokenWord = if (n == 1) "token" else "tokens"
        val counters = "put $n +1/+1 $counterWord on this permanent"
        val servos = "create $n 1/1 colorless Servo artifact creature $tokenWord"
        return TriggeredAbility(
            id = AbilityId(if (instance == 0) ABILITY_ID_PREFIX else "${ABILITY_ID_PREFIX}_$instance"),
            trigger = EventPattern.ZoneChangeEvent(to = Zone.BATTLEFIELD),
            binding = TriggerBinding.SELF,
            activeZones = setOf(Zone.BATTLEFIELD),
            effect = MayEffect(
                effect = AddCountersEffect(Counters.PLUS_ONE_PLUS_ONE, n, EffectTarget.Self),
                otherwise = CreateTokenEffect(
                    count = DynamicAmount.Fixed(n),
                    power = 1,
                    toughness = 1,
                    // Colorless: no colors, and no imageUri — the set-scoped TokenArtRegistry
                    // resolves the Servo art of whichever set printed the fabricate card, which a
                    // hard-coded URI here could not do.
                    colors = emptySet(),
                    creatureTypes = setOf("Servo"),
                    artifactToken = true,
                ),
                // Both outcomes are named in the prompt: declining is not "nothing happens", it is
                // the Servos, and a bare "You may put counters?" would hide that.
                descriptionOverride = "You may $counters. If you don't, $servos.",
            ),
            descriptionOverride = "When this permanent enters, you may $counters. If you don't, " +
                "$servos.",
        )
    }

    /**
     * The N of every printed `Fabricate N` on [cardDef], in printed order — empty when it has none.
     *
     * A list rather than a sum: CR 702.123b says each instance of fabricate triggers separately, so
     * two instances are two abilities with their own N, not one ability with the total. (Contrast
     * [Vanishing.printedCount], which *does* sum, because CR 702.62d stacks its entry counters.)
     */
    fun printedCounts(cardDef: CardDefinition): List<Int> =
        cardDef.keywordAbilities
            .filterIsInstance<KeywordAbility.Numeric>()
            .filter { it.keyword == Keyword.FABRICATE }
            .map { it.n }
}
