package com.wingedsheep.engine.mechanics.sba.permanent

import com.wingedsheep.engine.handlers.DecisionHandler
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.mechanics.sba.StateBasedActionCheck
import com.wingedsheep.engine.mechanics.sba.StateBasedActionModule
import com.wingedsheep.engine.registry.CardRegistry

class PermanentSbaModule(
    private val zones: ZoneTransitionService,
    private val decisionHandler: DecisionHandler,
    private val cardRegistry: CardRegistry
) : StateBasedActionModule {
    override fun checks(): List<StateBasedActionCheck> = listOf(
        DayNightCheck(cardRegistry),
        EndedDurationExpiryCheck(),
        AttachedCopyExpiryCheck(),
        AttackedPermanentRemovedFromCombatCheck(),
        PlaneswalkerLoyaltyCheck(zones),
        BattleDefenseCheck(zones),
        BattleProtectorCheck(zones),
        LegendRuleCheck(decisionHandler, cardRegistry),
        CounterAnnihilationCheck(),
        UnattachedAurasCheck(zones, cardRegistry),
        SoulbondPairingCheck(),
        SagaSacrificeCheck(zones, cardRegistry),
        CommanderZoneChoiceCheck(decisionHandler),
    )
}
