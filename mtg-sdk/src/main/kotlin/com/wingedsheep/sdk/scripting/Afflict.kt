package com.wingedsheep.sdk.scripting

import com.wingedsheep.sdk.scripting.effects.LoseLifeEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Afflict (CR 702.130) as a keyword-derived triggered ability.
 *
 * `Afflict N` means "Whenever this creature becomes blocked, defending player loses N life."
 * The number is carried by [KeywordAbility.Numeric]; the engine derives one instance of
 * [becomesBlockedTrigger] for each such ability, rather than making card definitions duplicate
 * the rules text.  The effect deliberately refers to [Player.DefendingPlayer], not an opponent
 * shortcut: in multiplayer this is the player (or the controller of the non-player defender)
 * that this attacker actually attacked.
 */
object Afflict {
    fun becomesBlockedTrigger(amount: Int, instance: Int): TriggeredAbility = TriggeredAbility(
        // Same-N instances are still distinct abilities (CR 702.130b), so their engine identity
        // must not collapse merely because their displayed amount is equal.
        id = AbilityId("afflict_${amount}_$instance"),
        trigger = EventPattern.BecomesBlockedEvent(),
        binding = TriggerBinding.SELF,
        effect = LoseLifeEffect(amount, EffectTarget.PlayerRef(Player.DefendingPlayer)),
        descriptionOverride = "Whenever this creature becomes blocked, defending player loses $amount life.",
    )
}
